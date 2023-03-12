package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.ocs;

import com.fasterxml.jackson.databind.JsonNode;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.config.NextcloudApiConfig;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudApiResponse;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudUser;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.model.NextcloudUserList;
import eu.planlos.pretixtonextcloudintegrator.api.pretix.service.PretixApiOrderService;
import eu.planlos.pretixtonextcloudintegrator.common.exception.ApiException;
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

@Slf4j
@Service
public class NextcloudApiUserService extends NextcloudApiService {

    private static final String SUCCESS_MESSAGE_CREATE_USER = "Created user: {}";
    private static final String FAIL_MESSAGE_CREATE_USER = "Could not create  user: {}, Error: {}";
    private static final String FAIL_MESSAGE_GET_USERS = "Could not load users from nextcloud";
    private static final String NC_API_USERS = "/ocs/v1.php/cloud/users";
    private static final String NC_API_SUFFIX_JSON = "?format=json";

    public NextcloudApiUserService(NextcloudApiConfig nextcloudApiConfig, @Qualifier("NextcloudWebClient") WebClient webClient, PretixApiOrderService pretixApiOrderService) {
        super(nextcloudApiConfig, webClient);
    }

    public List<String> getAllUsernames() {

        if(log.isDebugEnabled()) {
            String apiResponseString = webClient
                    .get()
                    .uri(buildUriGetUserlist())
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                    .doOnError(error -> log.error("{}: {}", FAIL_MESSAGE_GET_USERS, error.getMessage()))
                    .block();
            log.debug(apiResponseString);

            JsonNode jsonNode = webClient
                    .get()
                    .uri(buildUriGetUserlist())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            log.debug(jsonNode.toPrettyString());
        }

        NextcloudApiResponse<NextcloudUserList> apiResponse = webClient
                .get()
                .uri(buildUriGetUserlist())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudUserList>>(){})
                .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                .doOnError(error -> log.error("{}: {}", FAIL_MESSAGE_GET_USERS, error.getMessage()))
                .block();

        NextcloudUserList nextcloudUserList = apiResponse.getOcs().getData();
        return nextcloudUserList.getUsers();
    }

    public NextcloudUser getUser(String username) {

        if(log.isDebugEnabled()) {
            String apiResponseString = webClient
                    .get()
                    .uri(buildUriGetUser(username))
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                    .doOnError(error -> log.error("{}: {}", FAIL_MESSAGE_GET_USERS, error.getMessage()))
                    .block();
            log.debug(apiResponseString);
        }

        NextcloudApiResponse<NextcloudUser> apiResponse = webClient
                .get()
                .uri(buildUriGetUser(username))
                .retrieve()
                .bodyToMono(NextcloudApiResponse.class)
                .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                .doOnError(error -> log.error("{}: {}", FAIL_MESSAGE_GET_USERS, error.getMessage()))
                .block();

        //TODO obviously
        return apiResponse.getOcs().getData();
    }

    public String getUserEmail(String username) {
        NextcloudUser nextcloudUser = getUser(username);
        return nextcloudUser.getEmail();
    }

    public void createUser(String email, String name){

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("email", email);
        formData.add("displayName", name);

        try {
            String resultXML = webClient
                    .post()
                    .uri(buildUriCreateUser(email))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                    .doOnError(error -> log.error(FAIL_MESSAGE_CREATE_USER, email, error.getMessage()))
                    .block();

            if(resultXML!=null) {
                log.info(SUCCESS_MESSAGE_CREATE_USER, resultXML);
            }

            throw new ApiException(ApiException.Cause.IS_NULL);
        } catch (WebClientResponseException e) {
            throw new ApiException(e);
        }
    }

    /*
     * Uri generators
     */
    private String buildUriCreateUser(String email) {
        return String.format("%s", NC_API_USERS);
    }

    private String buildUriGetUserlist() {
        return String.format("%s%s", NC_API_USERS, NC_API_SUFFIX_JSON);
    }

    private String buildUriGetUser(String username) {
        return String.format("%s/%s%s", NC_API_USERS, username, NC_API_SUFFIX_JSON);
    }
}