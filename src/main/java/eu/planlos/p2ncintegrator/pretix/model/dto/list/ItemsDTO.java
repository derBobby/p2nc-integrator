package eu.planlos.p2ncintegrator.pretix.model.dto.list;

import eu.planlos.p2ncintegrator.pretix.model.dto.single.ItemDTO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ItemsDTO(
        @NotNull Integer count,
        String next,
        String previous,
        @NotNull List<ItemDTO> results
) {}