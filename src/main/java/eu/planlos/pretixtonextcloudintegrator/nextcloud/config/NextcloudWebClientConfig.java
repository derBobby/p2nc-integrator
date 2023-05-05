package eu.planlos.pretixtonextcloudintegrator.nextcloud.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.model.NextcloudApiResponse;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.ocs.NextcloudApiResponseDeserializer;
import eu.planlos.pretixtonextcloudintegrator.common.web.WebClientRequestFilter;
import eu.planlos.pretixtonextcloudintegrator.common.web.WebClientResponseFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class NextcloudWebClientConfig {

    @Bean
    @Qualifier("NextcloudWebClient")
    public static WebClient configureNextcloudWebClient(NextcloudApiConfig apiConfig) {

        log.info("Creating WebClient using:");
        log.info("- Nextcloud address: {}", apiConfig.address());
        log.info("- Nextcloud username: {}", apiConfig.user());
        log.info("- Nextcloud default group: {}", apiConfig.defaultGroup());

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    SimpleModule module = new SimpleModule();
                    module.addDeserializer(NextcloudApiResponse.class, new NextcloudApiResponseDeserializer<>());
                    module.addDeserializer(NextcloudApiResponse.class, new NextcloudApiResponseDeserializer<>());
                    objectMapper.registerModule(module);
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                })
                .build();

        return WebClient.builder()
                .baseUrl(apiConfig.address())
                .exchangeStrategies(exchangeStrategies)
                .filter(ExchangeFilterFunctions.basicAuthentication(apiConfig.user(), apiConfig.password()))
                .filter(WebClientRequestFilter.logRequest())
                .filter(WebClientResponseFilter.logResponse())
                .filter(WebClientResponseFilter.handleError())
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("OCS-APIRequest", "true");
                    httpHeaders.set("Accept", "application/json");
                })
                .build();
    }
}