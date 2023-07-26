package eu.planlos.pretixtonextcloudintegrator.pretix;

public interface IWebHookHandler {
    void handleApprovalNotification(String event, String code);

    void handleUserCreation(String event, String code);
}
