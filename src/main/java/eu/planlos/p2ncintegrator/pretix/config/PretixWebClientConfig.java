package eu.planlos.p2ncintegrator.pretix.config;

import eu.planlos.p2ncintegrator.common.web.WebClientRequestFilter;
import eu.planlos.p2ncintegrator.common.web.WebClientResponseFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class PretixWebClientConfig {

    @Bean
    @Qualifier("PretixWebClient")
    public static WebClient configurePretixWebClient(PretixApiConfig apiConfig) {

        String address = apiConfig.address();
        String token = apiConfig.token();
        String organizer = apiConfig.organizer();

        if (apiConfig.inactive()) {
            address = "mocked-address";
            token = "mocked-token";
            organizer = "mocked-organizer";
        }
        String finalToken = token;

        log.info("Creating WebClient using:");
        log.info("- Pretix address: {}", address);
        log.info("- Pretix organizer: {}", organizer);

        return WebClient.builder()
                .baseUrl(apiConfig.address())
                .filter(WebClientRequestFilter.logRequest())
                .filter(WebClientResponseFilter.logResponse())
                .filter(WebClientResponseFilter.handleError())
                .defaultHeaders(httpHeaders -> httpHeaders.set(HttpHeaders.AUTHORIZATION, String.join("", "Token ", finalToken)))
                .build();
    }
}