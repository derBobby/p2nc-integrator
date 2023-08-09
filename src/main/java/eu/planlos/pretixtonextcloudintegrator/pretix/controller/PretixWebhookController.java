package eu.planlos.pretixtonextcloudintegrator.pretix.controller;

import eu.planlos.pretixtonextcloudintegrator.common.audit.AuditService;
import eu.planlos.pretixtonextcloudintegrator.pretix.IPretixWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhook")
@Slf4j
 public class PretixWebhookController {

    private static final String ORDER_APPROVED = "pretix.event.order.approved";
    private static final String ORDER_NEED_APPROVAL = "pretix.event.order.placed.require_approval";

    private final AuditService webHookAuditService;
    private final IPretixWebHookHandler webHookHandler;

    public PretixWebhookController(AuditService webHookAuditService, IPretixWebHookHandler webHookHandler) {
        this.webHookAuditService = webHookAuditService;
        this.webHookHandler = webHookHandler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webHook(@RequestBody WebHookDTO hook) {

        MDC.put("orderCode", hook.code());
        log.info("Incoming webhook={}", hook);
        webHookAuditService.log(orderApprovalString(hook));

        // Event ID is set for later requests to the Pretix API.
        //      This avoids the need to loop the var through all methods and outside of the package
        String hookEvent = hook.event();
        String hookAction = hook.action();
        String hookCode = hook.code();

        if(hookAction.equals(ORDER_NEED_APPROVAL)) {
            webHookHandler.handleApprovalNotification(hookAction, hookEvent, hookCode);
            return;
        }

        if(hookAction.equals(ORDER_APPROVED)) {
            webHookHandler.handleUserCreation(hookAction, hookEvent, hookCode);
            return;
        }

        log.info("Webhook={} not relevant", hook);
    }

    private String orderApprovalString(WebHookDTO hook) {
        return String.format("Hook=%s reports order approval event for order=%s", hook.notification_id(), hook.code());
    }
}