package eu.planlos.p2ncintegrator.pretix.model.dto;

import eu.planlos.p2ncintegrator.pretix.model.validation.ValidAction;
import eu.planlos.p2ncintegrator.pretix.model.validation.ValidCode;
import eu.planlos.p2ncintegrator.pretix.model.validation.ValidEvent;
import eu.planlos.p2ncintegrator.pretix.model.validation.ValidOrganizer;
import jakarta.validation.constraints.NotNull;

public record WebHookDTO(
        @NotNull Long notification_id,
        @NotNull @ValidOrganizer String organizer,
        @NotNull @ValidEvent String event,
        @NotNull @ValidCode String code,
        @NotNull @ValidAction String action) {
}


