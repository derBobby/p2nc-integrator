package eu.planlos.p2ncintegrator.pretix.service.api;

import eu.planlos.p2ncintegrator.common.ApiException;
import eu.planlos.p2ncintegrator.pretix.config.PretixApiConfig;
import eu.planlos.p2ncintegrator.pretix.model.PretixId;
import eu.planlos.p2ncintegrator.pretix.model.dto.list.ItemsDTO;
import eu.planlos.p2ncintegrator.pretix.model.dto.single.ItemDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class PretixApiItemService extends PretixApiService {

    private static final String FETCH_MESSAGE = "Fetched item from Pretix: {}";

    public PretixApiItemService(PretixApiConfig pretixApiConfig, @Qualifier("PretixWebClient") WebClient webClient) {
        super(pretixApiConfig, webClient);
    }

    /*
     * Query
     */
    public List<ItemDTO> queryAllItems(String event) {

        if(isAPIDisabled()) {
            return Collections.emptyList();
        }

        ItemsDTO dto = webClient
                .get()
                .uri(itemListUri(event))
                .retrieve()
                .bodyToMono(ItemsDTO.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(3)))
                .doOnError(error -> log.error("Message fetching all items from Pretix API: {}", error.getMessage()))
                .block();

        if (dto != null) {
            List<ItemDTO> itemsDTOList = new ArrayList<>(dto.results());
            itemsDTOList.forEach(item -> log.info(FETCH_MESSAGE, item));
            return itemsDTOList;
        }

        throw new ApiException(ApiException.IS_NULL);
    }

    public ItemDTO queryItem(String event, PretixId itemId) {

        if(isAPIDisabled()) {
            return null;
        }

        ItemDTO itemDTO = webClient
                .get()
                .uri(specificItemUri(event, itemId.getValue()))
                .retrieve()
                .bodyToMono(ItemDTO.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(3)))
                .doOnError(error -> log.error("Message fetching item={} from Pretix API: {}", itemId, error.getMessage()))
                .block();
        if (itemDTO != null) {
            log.info(FETCH_MESSAGE, itemDTO);
            return itemDTO;
        }

        throw new ApiException(ApiException.IS_NULL);
    }

    /*
     * Uri generators
     */
    private String specificItemUri(String event, Long itemId) {
        return String.join("", itemListUri(event), Long.toString(itemId), "/");
    }

    private String itemListUri(String event) {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", event,
                "/items/");
    }
}