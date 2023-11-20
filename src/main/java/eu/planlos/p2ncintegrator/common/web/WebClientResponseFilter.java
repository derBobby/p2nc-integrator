package eu.planlos.p2ncintegrator.common.web;

import eu.planlos.p2ncintegrator.common.ApiException;
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
            log.debug("##################### BEGIN ResponseFilter #####################");

            try {
                if (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError()) {

                    int statusCode = response.statusCode().value();
                    String reasonPhrase = HttpStatus.resolve(statusCode).getReasonPhrase();

                    return response.bodyToMono(String.class)
                            .defaultIfEmpty(reasonPhrase)
                            .flatMap(body -> {
                                String message = String.format("Connection error http/%s: %s", statusCode, body);
                                log.error(message);
                                return Mono.error(new ApiException(String.format("Server connection error: %s", message)));
                            });
                } else {
                    return Mono.just(response);
                }
            } finally {
                log.debug("##################### END   ResponseFilter #####################");
            }
        });
    }

    /*
     * normal handling
     */
    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.debug("##################### BEGIN ResponseFilter #####################");
            logStatus(response);
            logHeaders(response);
            log.debug("##################### END   ResponseFilter #####################");
            return Mono.just(response);
        });
    }

    private static void logStatus(ClientResponse response) {
        int reasonCode = response.statusCode().value();
        String reasonPhrase = HttpStatus.resolve(reasonCode).getReasonPhrase();
        log.debug("Returned staus code {} ({})", reasonCode, reasonPhrase);
    }

    private static void logHeaders(ClientResponse response) {
        response.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.debug("{}={}", name, value)));
    }

}