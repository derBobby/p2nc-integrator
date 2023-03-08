package eu.planlos.pretixtonextcloudintegrator.api.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.api.pretix.config.PretixApiConfig;
import eu.planlos.pretixtonextcloudintegrator.api.pretix.model.OrderDTO;
import eu.planlos.pretixtonextcloudintegrator.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Service
public class PretixApiOrderService extends PretixApiService {

    private static final String FETCH_MESSAGE = "Fetched order from Pretix: {}";

    public PretixApiOrderService(PretixApiConfig pretixApiConfig, @Qualifier("PretixWebClient") WebClient webClient) {
        super(pretixApiConfig, webClient);
    }

    /*
     * Query
     */
    public OrderDTO queryOrder(String code) {
        try {
            OrderDTO orderDto = webClient
                    .get()
                    .uri(specificOrderUri(code))
                    .retrieve()
                    .bodyToMono(OrderDTO.class)
                    .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                    .doOnError(error -> log.error("Message fetching order={} from Pretix API: {}", code, error.getMessage()))
                    .block();
            if (orderDto != null) {
                log.info(FETCH_MESSAGE, orderDto);
                return orderDto;
            }

            throw new ApiException(ApiException.Cause.IS_NULL);
        } catch (WebClientResponseException e) {
            throw new ApiException(e);
        }
    }

    /*
     * Uri generators
     */
    private String specificOrderUri(String order) {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", pretixApiConfig.event(),
                "/orders/", order, "/");
    }
}