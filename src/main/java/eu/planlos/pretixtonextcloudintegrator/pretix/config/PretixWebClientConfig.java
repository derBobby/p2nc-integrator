package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import eu.planlos.pretixtonextcloudintegrator.common.web.WebClientRequestFilter;
import eu.planlos.pretixtonextcloudintegrator.common.web.WebClientResponseFilter;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixApiQuestionService2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
public class PretixWebClientConfig {

    @Bean
    @Qualifier("PretixWebClient")
    public static WebClient configurePretixWebClient(PretixApiConfig apiConfig) {

        log.info("Creating WebClient using:");
        log.info("- Pretix address: {}", apiConfig.address());
        log.info("- Pretix organizer: {}", apiConfig.organizer());
        log.info("- Pretix event: {}", apiConfig.event());

        return WebClient.builder()
                .baseUrl(apiConfig.address())
                .filter(WebClientRequestFilter.logRequest())
                .filter(WebClientResponseFilter.logResponse())
                .filter(WebClientResponseFilter.handleError())
                .defaultHeaders(httpHeaders -> httpHeaders.set(HttpHeaders.AUTHORIZATION, String.join("", "Token ", apiConfig.token())))
                .build();
    }

    @Bean
    PretixApiQuestionService2 pretixApiQuestionService2(@Qualifier("PretixWebClient") WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient)).build();
        return factory.createClient(PretixApiQuestionService2.class);
    }
}