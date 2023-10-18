package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto;

import jakarta.validation.constraints.Pattern;

public record WebHookDTO(
        Long notification_id,
        String organizer,
        String event,
        @Pattern(regexp = "^(?i)[a-z0-9]{5}$", message = "Invalid format. Should be alphanumeric and exactly 5 characters.")
        String code,
        String action) {
}