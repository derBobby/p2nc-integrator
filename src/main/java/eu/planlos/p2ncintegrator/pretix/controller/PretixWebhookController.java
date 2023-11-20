package eu.planlos.p2ncintegrator.pretix.controller;

import eu.planlos.p2ncintegrator.common.audit.AuditService;
import eu.planlos.p2ncintegrator.common.web.DtoValidationErrorHandler;
import eu.planlos.p2ncintegrator.pretix.IPretixWebHookHandler;
import eu.planlos.p2ncintegrator.pretix.model.dto.PretixSupportedActions;
import eu.planlos.p2ncintegrator.pretix.model.dto.WebHookDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
    public ResponseEntity<String> webHook(@Valid @RequestBody WebHookDTO hook, BindingResult bindingResult) {

        //TODO can this be moved to an annotation?
        DtoValidationErrorHandler.handle(bindingResult);

        log.info("Incoming webhook={}", hook);
        webHookAuditService.log(orderApprovalString(hook));

        PretixSupportedActions hookActionEnum = getAction(hook);
        String hookAction = hook.action();
        String hookEvent = hook.event();
        String hookCode = hook.code();

        if (hookActionEnum.equals(PretixSupportedActions.ORDER_NEED_APPROVAL)) {
            webHookHandler.handleApprovalNotification(hookAction, hookEvent, hookCode);
            return ResponseEntity.noContent().build();
        }

        if (hookActionEnum.equals(PretixSupportedActions.ORDER_APPROVED)) {
            Optional<String> optionalMessage = webHookHandler.handleUserCreation(hookAction, hookEvent, hookCode);
            return optionalMessage
                    .map(s -> ResponseEntity.ok().body(s))
                    .orElseGet(
                            () -> ResponseEntity.ok().body("WebHook is ignored, no filter matched")
                    );
        }

        throw new IllegalArgumentException("We should not reach this point.");
    }

    private PretixSupportedActions getAction(WebHookDTO hook) {
        log.debug("Looking up Enum for {}", hook.action());
        return PretixSupportedActions.getEnumByAction(hook.action());
    }

    private String orderApprovalString(WebHookDTO hook) {
        return String.format("Hook=%s reports order approval event for order=%s", hook.notification_id(), hook.code());
    }
}