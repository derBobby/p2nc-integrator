package eu.planlos.p2ncintegrator.common.notification.config;

import eu.planlos.p2ncintegrator.common.web.WebClientRequestFilter;
import eu.planlos.p2ncintegrator.common.web.WebClientResponseFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class SignalWebClientConfig {

    @Bean
    @Qualifier("SignalWebClient")
    public static WebClient configureSignalWebClient(SignalApiConfig apiConfig) {

        String address = apiConfig.address();
        String user = apiConfig.user();
        String password = apiConfig.password();

        if (apiConfig.inactive()) {
            address = "mocked-address";
            user = "mocked-user";
            password = "mocked-password";
        }

        log.info("Creating WebClient using:");
        log.info("- Signal address: {}", address);
        log.info("- Signal username: {}", user);

        return WebClient.builder()
                .baseUrl(address)
                .filter(WebClientRequestFilter.logRequest())
                .filter(WebClientResponseFilter.logResponse())
                .filter(WebClientResponseFilter.handleError())
                .filter(ExchangeFilterFunctions.basicAuthentication(user, password))
                .defaultHeaders(httpHeaders -> httpHeaders.set(HttpHeaders.ACCEPT, "application/json"))
                .build();
    }
}