package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.list;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.ItemCategoryDTO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ItemCategoriesDTO(
        @NotNull Integer count,
        String next,
        String previous,
        @NotNull List<ItemCategoryDTO> results
) {}