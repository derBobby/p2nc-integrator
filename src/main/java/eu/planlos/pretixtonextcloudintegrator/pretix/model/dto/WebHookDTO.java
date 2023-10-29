package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.ValidAction;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.ValidCode;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.ValidEvent;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.ValidOrganizer;
import jakarta.validation.constraints.NotNull;

public record WebHookDTO(
        @NotNull Long notification_id,
        @NotNull @ValidOrganizer String organizer,
        @NotNull @ValidEvent String event,
        @NotNull @ValidCode String code,
        @NotNull @ValidAction String action) {
}


