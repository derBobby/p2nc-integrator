package eu.planlos.p2ncintegrator.pretix.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.planlos.p2ncintegrator.pretix.model.validation.ValidAction;
import eu.planlos.p2ncintegrator.pretix.model.validation.ValidEvent;
import eu.planlos.p2ncintegrator.pretix.model.validation.ValidFilterMap;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record PretixQnaFilterUpdateDTO(
        @JsonProperty("id") @NotNull Long id,
        @JsonProperty("action") @NotNull @ValidAction String action,
        @JsonProperty("event") @NotNull @ValidEvent String event,
        @JsonProperty("qna-list") @NotNull @ValidFilterMap Map<String, List<String>> filterMap) {
}