package eu.planlos.p2ncintegrator.common.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

/**
 * Source
 * <a href="https://careydevelopment.us/blog/spring-webflux-how-to-log-requests-with-webclient">https://careydevelopment.us/blog/spring-webflux-how-to-log-requests-with-webclient</a>
 */
@Slf4j
public class WebClientRequestFilter {

    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("##################### BEGIN RequestFilter #####################");
            logMethodAndUrl(request);
            logRequestHeaders(request);
            log.info("##################### END   RequestFilter #####################");
            return Mono.just(request);
        });
    }

    private static void logMethodAndUrl(ClientRequest request) {
        log.info("{} to {}", request.method().name(), request.url());
    }

    private static void logRequestHeaders(ClientRequest request) {
        request.headers().forEach((name, values) -> values.stream().filter(value -> !name.contains("Authorization")).forEach(value -> log.debug("{}={}", name, value)));
    }
}