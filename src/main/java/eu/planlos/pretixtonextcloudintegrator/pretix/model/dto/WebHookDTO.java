package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto;

public record WebHookDTO(Long notification_id, String organizer, String event, String code, String action) {
}