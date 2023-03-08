package eu.planlos.pretixtonextcloudintegrator.api.pretix.config;

import eu.planlos.pretixtonextcloudintegrator.common.web.WebClientRequestFilter;
import eu.planlos.pretixtonextcloudintegrator.common.web.WebClientResponseFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PretixWebClientConfiguration {

    @Bean
    @Qualifier("PretixWebClient")
    public static WebClient configurePretixWebClient(PretixApiConfig apiConfig) {
        return WebClient.builder()
                .baseUrl(apiConfig.address())
                .filter(WebClientRequestFilter.logRequest())
                .filter(WebClientResponseFilter.logResponse())
                .filter(WebClientResponseFilter.handleError())
                .defaultHeaders(httpHeaders -> httpHeaders.set(HttpHeaders.AUTHORIZATION, String.join("", "Token ", apiConfig.apiToken())))
                .build();
    }
}