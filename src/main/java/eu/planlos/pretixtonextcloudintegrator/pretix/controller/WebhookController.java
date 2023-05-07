package eu.planlos.pretixtonextcloudintegrator.pretix.controller;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.common.audit.AuditService;
import eu.planlos.pretixtonextcloudintegrator.pretix.IWebHookHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhook")
@Slf4j
 public class WebhookController {

    private final AuditService webHookAuditService;
    private final IWebHookHandler webHookHandler;

    public WebhookController(AuditService webHookAuditService, IWebHookHandler webHookHandler) {
        this.webHookAuditService = webHookAuditService;
        this.webHookHandler = webHookHandler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webHook(@RequestBody WebHookDTO hook) {
        log.info("Incoming Webhook: {}", hook.toString());

        if (hook.action().equals("pretix.event.order.approved")) {
            webHookAuditService.log(orderApprovalString(hook));
            webHookHandler.handle(hook);
        } else if(hook.action().startsWith("pretix.event.order.")) {
            log.info("Hook {} reports order event for order {}", hook.notification_id(), hook.code());
        }
    }

    private String orderApprovalString(WebHookDTO hook) {
        return String.format("Hook %s reports order approval event for order %s", hook.notification_id(), hook.code());
    }
}