package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.config;

import eu.planlos.pretixtonextcloudintegrator.common.web.WebClientRequestFilter;
import eu.planlos.pretixtonextcloudintegrator.common.web.WebClientResponseFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NextcloudWebClientConfiguration {

    @Bean
    @Qualifier("NextcloudWebClient")
    public static WebClient configureNextcloudWebClient(NextcloudApiConfig apiConfig) {

        return WebClient.builder()
                .baseUrl(apiConfig.address())
                .filter(ExchangeFilterFunctions.basicAuthentication(apiConfig.user(), apiConfig.password()))
                .filter(WebClientRequestFilter.logRequest())
                .filter(WebClientResponseFilter.logResponse())
                .filter(WebClientResponseFilter.handleError())
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("OCS-APIRequest", "true");
                    httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
                    httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE);
                })
                .build();
    }
}