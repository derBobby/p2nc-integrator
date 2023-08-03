package eu.planlos.pretixtonextcloudintegrator.pretix;

public interface IPretixWebHookHandler {
    void handleApprovalNotification(String event, String code);

    void handleUserCreation(String event, String code);
}
