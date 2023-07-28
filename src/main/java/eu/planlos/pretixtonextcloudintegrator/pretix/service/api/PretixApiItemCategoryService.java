package eu.planlos.pretixtonextcloudintegrator.pretix.service.api;

import eu.planlos.pretixtonextcloudintegrator.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixContext;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixId;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.list.ItemCategoriesDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.ItemCategoryDTO;
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
public class PretixApiItemCategoryService extends PretixApiService {

    private static final String FETCH_MESSAGE = "Fetched item category from Pretix: {}";

    public PretixApiItemCategoryService(PretixApiConfig pretixApiConfig, @Qualifier("PretixWebClient") WebClient webClient, PretixContext pretixContext) {
        super(pretixApiConfig, webClient, pretixContext);
    }

    /*
     * Query
     */
    public List<ItemCategoryDTO> queryAllItemCategories() {

        ItemCategoriesDTO dto = webClient
                .get()
                .uri(itemCategoryListUri())
                .retrieve()
                .bodyToMono(ItemCategoriesDTO.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(3)))
                .doOnError(error -> log.error("Message fetching all item categories from Pretix API: {}", error.getMessage()))
                .block();

        if (dto != null) {
            List<ItemCategoryDTO> itemCategoryDTOList = new ArrayList<>(dto.results());
            itemCategoryDTOList.forEach(itemCategoryDTO -> log.info(FETCH_MESSAGE, itemCategoryDTO));
            return itemCategoryDTOList;
        }

        throw new ApiException(ApiException.IS_NULL);
    }

    public ItemCategoryDTO queryItemCategory(PretixId itemCategoryId) {
        ItemCategoryDTO itemCategoryDTO = webClient
                .get()
                .uri(specificItemCategoryUri(itemCategoryId.getValue()))
                .retrieve()
                .bodyToMono(ItemCategoryDTO.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(3)))
                .doOnError(error -> log.error("Message fetching item category={} from Pretix API: {}", itemCategoryId, error.getMessage()))
                .block();
        if (itemCategoryDTO != null) {
            log.info(FETCH_MESSAGE, itemCategoryDTO);
            return itemCategoryDTO;
        }

        throw new ApiException(ApiException.IS_NULL);
    }

    /*
     * Uri generators
     */
    private String specificItemCategoryUri(Long itemCategoryId) {
        return String.join("", itemCategoryListUri(), Long.toString(itemCategoryId), "/");
    }

    private String itemCategoryListUri() {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", pretixEvent(),
                "/categories/");
    }
}