package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.ocs;

import com.fasterxml.jackson.databind.JsonNode;
import eu.planlos.pretixtonextcloudintegrator.api.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.config.NextcloudApiConfig;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudApiResponse;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudResponse;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudUser;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudUserList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NextcloudApiUserService extends NextcloudApiService {

    private static final String SUCCESS_MESSAGE_CREATE_USER = "Created user: {}";
    private static final String FAIL_MESSAGE_CREATE_USER = "Could not create  user: {}, Error: {}";
    private static final String FAIL_MESSAGE_GET_USERS = "Could not load users from nextcloud";
    private static final String NC_API_USERS = "/ocs/v1.php/cloud/users";
    private static final String NC_API_SUFFIX_JSON = "?format=json";

    public NextcloudApiUserService(NextcloudApiConfig nextcloudApiConfig, @Qualifier("NextcloudWebClient") WebClient webClient ) {
        super(nextcloudApiConfig, webClient);
    }

    public List<String> getAllUseridsFromNextcloud() {

        if(log.isDebugEnabled()) {
            JsonNode jsonNode = webClient
                    .get()
                    .uri(buildUriGetUserList())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            log.debug(jsonNode.toPrettyString());
        }

        NextcloudApiResponse<NextcloudUserList> apiResponse = webClient
                .get()
                .uri(buildUriGetUserList())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudUserList>>(){})
                .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                .doOnError(error -> log.error("{}: {}", FAIL_MESSAGE_GET_USERS, error.getMessage()))
                .block();

        NextcloudUserList nextcloudUseridList = apiResponse.getData();
        return nextcloudUseridList.getUsers();
    }

    public NextcloudUser getUser(String username) {

        if(log.isDebugEnabled()) {
            JsonNode jsonNode = webClient
                    .get()
                    .uri(buildUriGetUser(username))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                    .doOnError(error -> log.error("{}: {}", FAIL_MESSAGE_GET_USERS, error.getMessage()))
                    .block();
            log.debug(jsonNode.toPrettyString());
        }

        NextcloudApiResponse<NextcloudUser> apiResponse = webClient
                .get()
                .uri(buildUriGetUser(username))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudUser>>(){})
                .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                .doOnError(error -> log.error("{}: {}", FAIL_MESSAGE_GET_USERS, error.getMessage()))
                .block();

        return apiResponse.getData();
    }

    public Map<String, String> getAllUsersAsUseridEmailMap() {

        List<String> useridList = getAllUseridsFromNextcloud();
        return useridList.stream().collect(Collectors.toMap(
                userid -> userid,
                userid -> getUser(userid).getEmail()
        ));
    }

    public void createUser(String userid, String email, String firstName, String lastName){

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("userid", userid);
        formData.add("email", email);
        formData.add("displayName", String.format("%s %s", firstName, lastName));
        formData.add("groups[]", nextcloudApiConfig.defaultGroup());

        try {
            NextcloudApiResponse<NextcloudResponse> apiResponse = webClient
                    .post()
                    .uri(buildUriCreateUser())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudResponse>>(){})
                    .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                    .doOnError(error -> log.error(FAIL_MESSAGE_CREATE_USER, email, error.getMessage()))
                    .block();

            //TODO error handling
            if (apiResponse == null) {
                throw new ApiException("ApiResponse object is NULL");
            }
            if(apiResponse.getMeta().getStatus().equals("failure")) {
                throw new ApiException(String.format("Status is 'failure': %s", apiResponse));
            }
            log.debug(SUCCESS_MESSAGE_CREATE_USER, apiResponse.getMeta());
        } catch (WebClientResponseException e) {
            throw new ApiException(e);
        }
    }

    /*
     * Uri generators
     */
    private String buildUriCreateUser() {
        return String.format("%s%s", NC_API_USERS, NC_API_SUFFIX_JSON);
    }

    private String buildUriGetUserList() {
        return String.format("%s%s", NC_API_USERS, NC_API_SUFFIX_JSON);
    }

    private String buildUriGetUser(String username) {
        return String.format("%s/%s%s", NC_API_USERS, username, NC_API_SUFFIX_JSON);
    }

}
