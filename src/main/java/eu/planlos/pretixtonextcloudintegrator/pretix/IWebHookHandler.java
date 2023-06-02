package eu.planlos.pretixtonextcloudintegrator.pretix;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;

public interface IWebHookHandler {
    void handleApprovalNotification(String hook);

    void handleUserCreation(WebHookDTO hook);
}
