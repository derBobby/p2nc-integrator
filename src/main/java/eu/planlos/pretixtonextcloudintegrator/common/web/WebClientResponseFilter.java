package eu.planlos.pretixtonextcloudintegrator.common.web;

import eu.planlos.pretixtonextcloudintegrator.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

/**
 * Source:
 * <a href="https://careydevelopment.us/blog/spring-webflux-how-to-log-responses-with-webclient">https://careydevelopment.us/blog/spring-webflux-how-to-log-responses-with-webclient</a>
 */
@Slf4j
public class WebClientResponseFilter {

    /*
     * Error handling
     */
    public static ExchangeFilterFunction handleError() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("##################### BEGIN ResponseFilter #####################");

            try {
                if (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError()) {

                    int statusCode = response.statusCode().value();
                    String reasonPhrase = HttpStatus.resolve(statusCode).getReasonPhrase();

                    return response.bodyToMono(String.class)
                            .defaultIfEmpty(reasonPhrase)
                            .flatMap(body -> {
                                log.error("Body is {}", body);
                                return Mono.error(new ApiException(body, statusCode));
                            });
                } else {
                    return Mono.just(response);
                }
            } finally {
                log.info("##################### END   ResponseFilter #####################");
            }
        });
    }

    /*
     * normal handling
     */
    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("##################### BEGIN ResponseFilter #####################");
            logStatus(response);
            logHeaders(response);
            log.info("##################### END   ResponseFilter #####################");
            return Mono.just(response);
        });
    }

    private static void logStatus(ClientResponse response) {
        int reasonCode = response.statusCode().value();
        String reasonPhrase = HttpStatus.resolve(reasonCode).getReasonPhrase();
        log.info("Returned staus code {} ({})", reasonCode, reasonPhrase);
    }

    private static void logHeaders(ClientResponse response) {
        response.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.debug("{}={}", name, value)));
    }

}