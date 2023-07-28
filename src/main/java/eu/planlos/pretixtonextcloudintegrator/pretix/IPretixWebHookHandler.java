package eu.planlos.pretixtonextcloudintegrator.pretix;

public interface IPretixWebHookHandler {
    void handleApprovalNotification(String code);

    void handleUserCreation(String code);
}
