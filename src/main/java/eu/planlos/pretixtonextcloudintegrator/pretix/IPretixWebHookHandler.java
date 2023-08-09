package eu.planlos.pretixtonextcloudintegrator.pretix;

public interface IPretixWebHookHandler {
    void handleApprovalNotification(String action, String event, String code);

    void handleUserCreation(String action, String event, String code);
}
