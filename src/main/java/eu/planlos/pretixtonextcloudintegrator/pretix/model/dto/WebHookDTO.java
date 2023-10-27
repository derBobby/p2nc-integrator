package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record WebHookDTO(
        @NotNull Long notification_id,
        @NotNull @Pattern(regexp = "(?i)^[a-z0-9_-]{1,30}$", message = "Invalid organizer") String organizer,
        @NotNull @Pattern(regexp = "(?i)^[a-z0-9_-]{1,30}$", message = "Invalid event")String event,
        @NotNull @Pattern(regexp = "(?i)^[a-z0-9_-]{5}$", message = "Invalid code") String code,
        @NotNull @ValidAction String action) {
}


