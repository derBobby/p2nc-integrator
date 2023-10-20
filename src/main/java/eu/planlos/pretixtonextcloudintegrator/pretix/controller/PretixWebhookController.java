package eu.planlos.pretixtonextcloudintegrator.pretix.controller;

import eu.planlos.pretixtonextcloudintegrator.common.audit.AuditService;
import eu.planlos.pretixtonextcloudintegrator.pretix.IPretixWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.WebHookDTOSupportedAction;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.WebHookDTONotValidException;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTOValidator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhook")
@Slf4j
public class PretixWebhookController {

    private final AuditService webHookAuditService;
    private final IPretixWebHookHandler webHookHandler;

    public PretixWebhookController(AuditService webHookAuditService, IPretixWebHookHandler webHookHandler) {
        this.webHookAuditService = webHookAuditService;
        this.webHookHandler = webHookHandler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webHook(@RequestBody WebHookDTO hook) {

        new WebHookDTOValidator().validateOrThrowException(hook);
        //TODO Test
        WebHookDTOSupportedAction hookActionEnum = getAction(hook);

        // Add order code to log output
        MDC.put("orderCode", hook.code());
        log.info("Incoming webhook={}", hook);
        webHookAuditService.log(orderApprovalString(hook));

        // Event ID is set for later requests to the Pretix API.
        //      This avoids the need to loop the var through all methods and outside of the package
        String hookEvent = hook.event();
        String hookAction = hook.action();
        String hookCode = hook.code();

        //TODO replace hookAction String with Enum everywhere?
        if (hookActionEnum.equals(WebHookDTOSupportedAction.ORDER_NEED_APPROVAL)) {
            webHookHandler.handleApprovalNotification(hookAction, hookEvent, hookCode);
            return;
        }

        if (hookActionEnum.equals(WebHookDTOSupportedAction.ORDER_APPROVED)) {
            webHookHandler.handleUserCreation(hookAction, hookEvent, hookCode);
            return;
        }

        log.info("Webhook={} not relevant", hook);
    }

    private WebHookDTOSupportedAction getAction(WebHookDTO hook) {
        log.debug("Looking up Enum for {}", hook.action());
        try {
            return WebHookDTOSupportedAction.getEnumByAction(hook.action());
        } catch (IllegalArgumentException e) {
            throw new WebHookDTONotValidException(e.getMessage());
        }
    }

    private String orderApprovalString(WebHookDTO hook) {
        return String.format("Hook=%s reports order approval event for order=%s", hook.notification_id(), hook.code());
    }

    //TODO Test return code and message
    @ExceptionHandler(WebHookDTONotValidException.class)
    public ResponseEntity<String> handleWebHookDTONotValidException(WebHookDTONotValidException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}