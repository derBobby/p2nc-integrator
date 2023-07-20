package eu.planlos.pretixtonextcloudintegrator.pretix.controller;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.common.audit.AuditService;
import eu.planlos.pretixtonextcloudintegrator.pretix.IWebHookHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhook")
@Slf4j
 public class WebhookController {

    private static final String ORDER_APPROVED = "pretix.event.order.approved";
    private static final String ORDER_NEED_APPROVAL = "pretix.event.order.placed.require_approval";

    private final AuditService webHookAuditService;
    private final IWebHookHandler webHookHandler;

    public WebhookController(AuditService webHookAuditService, IWebHookHandler webHookHandler) {
        this.webHookAuditService = webHookAuditService;
        this.webHookHandler = webHookHandler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webHook(@RequestBody WebHookDTO hook) {
        log.info("Incoming webhook={}", hook);

        webHookAuditService.log(orderApprovalString(hook));
        String hookAction = hook.action();

        //TODO Add tests for the cases
        if(hookAction.equals(ORDER_NEED_APPROVAL)) {
            webHookHandler.handleApprovalNotification(hook.code());
            return;
        }

        if(hookAction.equals(ORDER_APPROVED)) {
            webHookHandler.handleUserCreation(hook);
            return;
        }

        log.info("Webhook={} not relevant", hook);
    }

    private String orderApprovalString(WebHookDTO hook) {
        return String.format("Hook=%s reports order approval event for order=%s", hook.notification_id(), hook.code());
    }
}