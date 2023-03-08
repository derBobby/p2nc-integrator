package eu.planlos.pretixtonextcloudintegrator.api.pretix.webhook.model;

public record WebHookDTO(Long notification_id, String organizer, String event, String code, String action) {
}