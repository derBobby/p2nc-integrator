package eu.planlos.pretixtonextcloudintegrator.pretix;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.WebHookDTO;

public interface IWebHookHandler {
    void handleApprovalNotification(String hook);

    void handleUserCreation(WebHookDTO hook);
}
