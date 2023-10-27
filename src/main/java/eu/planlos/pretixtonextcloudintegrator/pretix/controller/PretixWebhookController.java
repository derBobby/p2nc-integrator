package eu.planlos.pretixtonextcloudintegrator.pretix.controller;

import eu.planlos.pretixtonextcloudintegrator.common.audit.AuditService;
import eu.planlos.pretixtonextcloudintegrator.pretix.IPretixWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.PretixSupportedActions;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

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
    public void webHook(@Valid @RequestBody WebHookDTO hook, BindingResult bindingResult) {

        //TODO can this be automated?
        handleValidationErrors(bindingResult);

        //TODO Test, Action enum directly into WebHookDTO
        PretixSupportedActions hookActionEnum = getAction(hook);

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

    private void handleValidationErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Validation errors are present
            Map<String, String> validationErrors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                validationErrors.put(error.getField(), error.getDefaultMessage());
            }

            // Construct and return an error response as a Map
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Validation Error");
            errorResponse.put("errors", validationErrors);

            // Return the error response with a 400 Bad Request status
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorResponse.toString());
        }
    }

    private PretixSupportedActions getAction(WebHookDTO hook) {
        log.debug("Looking up Enum for {}", hook.action());
        return PretixSupportedActions.getEnumByAction(hook.action());
    }

    private String orderApprovalString(WebHookDTO hook) {
        return String.format("Hook=%s reports order approval event for order=%s", hook.notification_id(), hook.code());
    }
}