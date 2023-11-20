package eu.planlos.p2ncintegrator.pretix.model.dto.single;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record ItemDTO(
        @Id @NotNull Long id,
        @NotNull @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY) Map<String, String> name,
        @NotNull List<ItemVariationDTO> variations,
        @NotNull Long category
) {
    public String getName() {
        return name.get("de-informal");
    }
}