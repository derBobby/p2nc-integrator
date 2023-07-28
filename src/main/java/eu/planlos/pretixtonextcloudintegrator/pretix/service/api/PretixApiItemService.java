package eu.planlos.pretixtonextcloudintegrator.pretix.service.api;

import eu.planlos.pretixtonextcloudintegrator.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixContext;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixId;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.list.ItemsDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.ItemDTO;
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
public class PretixApiItemService extends PretixApiService {

    private static final String FETCH_MESSAGE = "Fetched item from Pretix: {}";

    public PretixApiItemService(PretixApiConfig pretixApiConfig, @Qualifier("PretixWebClient") WebClient webClient, PretixContext pretixContext) {
        super(pretixApiConfig, webClient, pretixContext);
    }

    /*
     * Query
     */
    public List<ItemDTO> queryAllItems() {

        ItemsDTO dto = webClient
                .get()
                .uri(itemListUri())
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

    public ItemDTO queryItem(PretixId itemId) {
        ItemDTO itemDTO = webClient
                .get()
                .uri(specificItemUri(itemId.getValue()))
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
    private String specificItemUri(Long itemId) {
        return String.join("", itemListUri(), Long.toString(itemId), "/");
    }

    private String itemListUri() {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", pretixEvent(),
                "/items/");
    }
}