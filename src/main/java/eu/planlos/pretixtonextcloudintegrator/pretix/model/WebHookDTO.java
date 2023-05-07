package eu.planlos.pretixtonextcloudintegrator.pretix.model;

public record WebHookDTO(Long notification_id, String organizer, String event, String code, String action) {
}