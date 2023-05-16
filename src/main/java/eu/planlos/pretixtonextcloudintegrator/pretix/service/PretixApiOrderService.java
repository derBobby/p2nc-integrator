package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.OrderDTO;
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
    public OrderDTO fetchOrderFromPretix(String code) {
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

            throw new ApiException("ApiResponse object is NULL");
        } catch (WebClientResponseException e) {
            throw new ApiException(e);
        }
    }

    /*
     * Uri generators
     */
    private String specificOrderUri(String orderCode) {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", pretixApiConfig.event(),
                "/orders/", orderCode, "/");
    }

    public String getEventUrl(String orderCode) {
        return String.join(
                "",
                pretixApiConfig.address(),
                "/control/event/", pretixApiConfig.organizer(),
                "/", pretixApiConfig.event(),
                "/orders/", orderCode, "/");
    }
}