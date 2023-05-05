package eu.planlos.pretixtonextcloudintegrator.pretix;

import eu.planlos.pretixtonextcloudintegrator.pretix.webhook.model.WebHookDTO;

public interface IWebHookHandler {
    void handle(WebHookDTO webHookDTO);
}
