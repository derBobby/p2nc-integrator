package eu.planlos.pretixtonextcloudintegrator.pretix.service.api;

import eu.planlos.pretixtonextcloudintegrator.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.common.web.PretixContext;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.list.OrdersDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PretixApiOrderService extends PretixApiService {

    private static final String FETCH_MESSAGE = "Fetched order from Pretix: {}";

    public PretixApiOrderService(PretixApiConfig pretixApiConfig, @Qualifier("PretixWebClient") WebClient webClient, PretixContext pretixContext) {
        super(pretixApiConfig, webClient, pretixContext);
    }

    /*
     * Query
     */
    public List<OrderDTO> fetchAllOrders() {

        OrdersDTO dto = webClient
                .get()
                .uri(orderListUri())
                .retrieve()
                .bodyToMono(OrdersDTO.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(3)))
                .doOnError(error -> log.error("Message fetching all orders from Pretix API: {}", error.getMessage()))
                .block();

        if (dto != null) {
            List<OrderDTO> orderDTOList = new ArrayList<>(dto.results());
            orderDTOList.forEach(order -> log.info(FETCH_MESSAGE, order));
            return orderDTOList;
        }

        throw new ApiException(ApiException.IS_NULL);
    }

    public OrderDTO fetchOrderFromPretix(String code) {
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

        throw new ApiException(ApiException.IS_NULL);
    }

    /*
     * Uri generators
     */
    private String specificOrderUri(String orderCode) {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", pretixEvent(),
                "/orders/", orderCode, "/");
    }

    private String orderListUri() {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", pretixEvent(),
                "/orders/");
    }

    public String getEventUrl(String orderCode) {
        return String.join(
                "",
                pretixApiConfig.address(),
                "/control/event/", pretixApiConfig.organizer(),
                "/", pretixEvent(),
                "/orders/", orderCode, "/");
    }
}