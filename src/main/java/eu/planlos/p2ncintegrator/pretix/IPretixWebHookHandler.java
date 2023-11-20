package eu.planlos.p2ncintegrator.pretix;

import java.util.Optional;

public interface IPretixWebHookHandler {
    void handleApprovalNotification(String action, String event, String code);

    Optional<String> handleUserCreation(String action, String event, String code);
}
