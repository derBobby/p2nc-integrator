package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.ValidAction;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.ValidCode;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.ValidEvent;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.ValidOrganizer;
import jakarta.validation.constraints.NotNull;

public record WebHookDTO(
        @NotNull Long notification_id,
        @ValidOrganizer String organizer,
        @ValidEvent String event,
        @ValidCode String code,
        @ValidAction String action) {
}


