package eu.planlos.pretixtonextcloudintegrator.api.pretix.webhook.service;

import eu.planlos.pretixtonextcloudintegrator.api.pretix.webhook.model.WebHookDTO;

public interface IWebHookHandler {
    void handle(WebHookDTO webHookDTO);
}
