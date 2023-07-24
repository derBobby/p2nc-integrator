package eu.planlos.pretixtonextcloudintegrator.nextcloud.service;

import eu.planlos.pretixtonextcloudintegrator.TestDataUtility;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.config.NextcloudApiConfig;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService.NC_API_USERLIST_JSON_URL;
import static eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService.NC_API_USER_JSON_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.client.WebClient.*;

@ExtendWith(MockitoExtension.class)
public class NextcloudApiUserServiceTest extends TestDataUtility {

    private static final NextcloudApiConfig apiConfig = new NextcloudApiConfig(true, "https://localhost/{userId}", "username", "password", "group");

    private static RequestBodyUriSpec requestBodyUriMock;
    @SuppressWarnings("rawtypes")
    private static RequestHeadersSpec requestHeadersMock;
    @SuppressWarnings("rawtypes")
    private static RequestHeadersUriSpec requestHeadersUriSpec;
    private static RequestBodySpec requestBodyMock;
    private static ResponseSpec responseMock;
    private static WebClient webClientMock;

    @BeforeAll
    static void mockWebClient() {
        requestBodyUriMock = mock(RequestBodyUriSpec.class);
        requestHeadersMock = mock(RequestHeadersSpec.class);
        requestHeadersUriSpec = mock(RequestHeadersUriSpec.class);
        requestBodyMock = mock(RequestBodySpec.class);
        responseMock = mock(ResponseSpec.class);
        webClientMock = mock(WebClient.class);
        when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
        when(webClientMock.post()).thenReturn(requestBodyUriMock);
    }

    @Test
    public void mailAndUserIdAvailable_accountCreationSuccessful() {
        // Prepare
        //      objects
        NextcloudApiUserService nextcloudApiUserService = new NextcloudApiUserService(apiConfig, webClientMock);
        NextcloudUserList takenUserList = new NextcloudUserList();
        //      webclient
        NextcloudApiResponse<NextcloudUserList> apiResponseUserList = new NextcloudApiResponse<>(new NextcloudMeta(), takenUserList);
        NextcloudResponse response = new NextcloudResponse();
        NextcloudApiResponse<NextcloudResponse> apiResponse = new NextcloudApiResponse<>(okMeta(), response);
        //      methods
        when(responseMock.bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudResponse>>() {})).thenReturn(Mono.just(apiResponse));
        when(requestHeadersUriSpec.uri(eq(NC_API_USERLIST_JSON_URL))).thenReturn(requestHeadersMock);

        when(responseMock.bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudUserList>>() {})).thenReturn(Mono.just(apiResponseUserList));
        when(requestBodyUriMock.uri(eq(NC_API_USERLIST_JSON_URL))).thenReturn(requestBodyMock);
        when(requestBodyMock.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(requestBodyMock);
        when(requestBodyMock.bodyValue(anyMap())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);

        // Act
        String userId = nextcloudApiUserService.createUser("newuser@example.com", "New", "User");

        // Check
        assertEquals(userId, "kv-kraichgau-nuser");
    }

    @Test
    public void mailAlreadyInUse_accountCreationFails() {
        // Prepare
        //      objects
        NextcloudApiUserService nextcloudApiUserService = new NextcloudApiUserService(apiConfig, webClientMock);
        NextcloudUser takenUser = takenUser();
        NextcloudUserList takenUserList = new NextcloudUserList(List.of(takenUser.id()));
        //      webclient
        NextcloudApiResponse<NextcloudUserList> apiResponseUserList = new NextcloudApiResponse<>(new NextcloudMeta(), takenUserList);
        NextcloudApiResponse<NextcloudUser> apiResponseUser = new NextcloudApiResponse<>(new NextcloudMeta(), takenUser);
        String uriUser = String.format(NC_API_USER_JSON_URL, takenUser.id());
        //      methods
        when(requestBodyMock.retrieve()).thenReturn(responseMock);

        when(requestHeadersUriSpec.uri(eq(NC_API_USERLIST_JSON_URL))).thenReturn(requestBodyMock);
        when(responseMock.bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudUserList>>(){})).thenReturn(Mono.just(apiResponseUserList));

        when(requestHeadersUriSpec.uri(eq(uriUser))).thenReturn(requestBodyMock);
        when(responseMock.bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudUser>>(){})).thenReturn(Mono.just(apiResponseUser));

        // Act
        // Check
        assertThrows(AccountCreationException.class, () -> nextcloudApiUserService.createUser(takenUser.email(), "New", "User"));
    }

    @Test
    public void mailAvailableUserIdTaken_createsAlternateId() {
        // Prepare
        //      objects
        NextcloudApiUserService nextcloudApiUserService = new NextcloudApiUserService(apiConfig, webClientMock);
        NextcloudUser takenUser = takenUser();
        NextcloudUserList takenUserList = new NextcloudUserList(List.of(takenUser.id()));
        //      webclient
        NextcloudApiResponse<NextcloudUserList> apiResponseUserList = new NextcloudApiResponse<>(new NextcloudMeta(), takenUserList);
        NextcloudApiResponse<NextcloudUser> apiResponseUser = new NextcloudApiResponse<>(new NextcloudMeta(), takenUser);

        NextcloudResponse response = new NextcloudResponse();
        NextcloudApiResponse<NextcloudResponse> apiResponse = new NextcloudApiResponse<>(okMeta(), response);

        String uriUser = String.format(NC_API_USER_JSON_URL, takenUser.id());
        //      methods
        when(responseMock.bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudResponse>>() {})).thenReturn(Mono.just(apiResponse));
        when(requestHeadersUriSpec.uri(eq(NC_API_USERLIST_JSON_URL))).thenReturn(requestHeadersMock);

        when(requestHeadersUriSpec.uri(eq(uriUser))).thenReturn(requestBodyMock);
        when(responseMock.bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudUser>>(){})).thenReturn(Mono.just(apiResponseUser));
        when(requestBodyMock.retrieve()).thenReturn(responseMock);

        when(responseMock.bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudUserList>>() {})).thenReturn(Mono.just(apiResponseUserList));
        when(requestBodyUriMock.uri(eq(NC_API_USERLIST_JSON_URL))).thenReturn(requestBodyMock);
        when(requestBodyMock.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(requestBodyMock);
        when(requestBodyMock.bodyValue(anyMap())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);

        // Act
        String userId = nextcloudApiUserService.createUser("newuser@example.com", "Display", "Name");

        // Check
        assertEquals("kv-kraichgau-diname", userId);
    }

        @Test
    public void firstnameNotLongEnoughToAvoidExisting_accountCreationFails() {
            // Prepare
            //      objects
            NextcloudApiUserService nextcloudApiUserService = new NextcloudApiUserService(apiConfig, webClientMock);
            NextcloudUser takenUser = takenUser();
            NextcloudUserList takenUserList = new NextcloudUserList(List.of(takenUser.id()));
            //      webclient
            NextcloudApiResponse<NextcloudUserList> apiResponseUserList = new NextcloudApiResponse<>(new NextcloudMeta(), takenUserList);
            NextcloudApiResponse<NextcloudUser> apiResponseUser = new NextcloudApiResponse<>(new NextcloudMeta(), takenUser);

            NextcloudResponse response = new NextcloudResponse();
            NextcloudApiResponse<NextcloudResponse> apiResponse = new NextcloudApiResponse<>(okMeta(), response);

            String uriUser = String.format(NC_API_USER_JSON_URL, takenUser.id());
            //      methods
            when(responseMock.bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudResponse>>() {
            })).thenReturn(Mono.just(apiResponse));
            when(requestHeadersUriSpec.uri(eq(NC_API_USERLIST_JSON_URL))).thenReturn(requestHeadersMock);

            when(requestHeadersUriSpec.uri(eq(uriUser))).thenReturn(requestBodyMock);
            when(responseMock.bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudUser>>() {
            })).thenReturn(Mono.just(apiResponseUser));
            when(requestBodyMock.retrieve()).thenReturn(responseMock);

            when(responseMock.bodyToMono(new ParameterizedTypeReference<NextcloudApiResponse<NextcloudUserList>>() {
            })).thenReturn(Mono.just(apiResponseUserList));
            when(requestBodyUriMock.uri(eq(NC_API_USERLIST_JSON_URL))).thenReturn(requestBodyMock);
            when(requestBodyMock.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(requestBodyMock);
            when(requestBodyMock.bodyValue(anyMap())).thenReturn(requestHeadersMock);
            when(requestHeadersMock.retrieve()).thenReturn(responseMock);

            // Act
            // Check
            assertThrows(AccountCreationException.class, () -> nextcloudApiUserService.createUser("newuser@example.com", "D", "Name"));

        }
}