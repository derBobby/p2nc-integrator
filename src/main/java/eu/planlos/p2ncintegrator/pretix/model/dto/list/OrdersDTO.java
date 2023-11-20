package eu.planlos.p2ncintegrator.pretix.model.dto.list;


import eu.planlos.p2ncintegrator.pretix.model.dto.single.OrderDTO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrdersDTO(
        @NotNull Integer count,
        String next,
        String previous,
        @NotNull List<OrderDTO> results
) {}