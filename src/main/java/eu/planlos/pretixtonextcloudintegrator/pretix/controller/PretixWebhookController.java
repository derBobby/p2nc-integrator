package eu.planlos.pretixtonextcloudintegrator.pretix.controller;

import eu.planlos.pretixtonextcloudintegrator.common.audit.AuditService;
import eu.planlos.pretixtonextcloudintegrator.pretix.IPretixWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.PretixSupportedActions;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PretixWebhookController.URL_WEBHOOK)
@Slf4j
public class PretixWebhookController {

    public static final String URL_WEBHOOK = "/api/v1/webhook";

    private final AuditService webHookAuditService;
    private final IPretixWebHookHandler webHookHandler;

    public PretixWebhookController(AuditService webHookAuditService, IPretixWebHookHandler webHookHandler) {
        this.webHookAuditService = webHookAuditService;
        this.webHookHandler = webHookHandler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webHook(@Valid @RequestBody WebHookDTO hook, BindingResult bindingResult) {
        WebHookValidationErrorHandler.handle(bindingResult);

        // Add order code to log output
        log.info("Incoming webhook={}", hook);
        webHookAuditService.log(orderApprovalString(hook));

        PretixSupportedActions hookActionEnum = getAction(hook);
        String hookAction = hook.action();
        String hookEvent = hook.event();
        String hookCode = hook.code();

        //TODO replace hookAction String with Enum everywhere?
        if (hookActionEnum.equals(PretixSupportedActions.ORDER_NEED_APPROVAL)) {
            webHookHandler.handleApprovalNotification(hookAction, hookEvent, hookCode);
            return;
        }

        if (hookActionEnum.equals(PretixSupportedActions.ORDER_APPROVED)) {
            webHookHandler.handleUserCreation(hookAction, hookEvent, hookCode);
            return;
        }

        log.info("Webhook={} not relevant", hook);
    }

    private PretixSupportedActions getAction(WebHookDTO hook) {
        log.debug("Looking up Enum for {}", hook.action());
        return PretixSupportedActions.getEnumByAction(hook.action());
    }

    private String orderApprovalString(WebHookDTO hook) {
        return String.format("Hook=%s reports order approval event for order=%s", hook.notification_id(), hook.code());
    }
}