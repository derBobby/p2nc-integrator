package eu.planlos.pretixtonextcloudintegrator.pretix;

public interface IWebHookHandler {
    void handleApprovalNotification(String code);

    void handleUserCreation(String code);
}
