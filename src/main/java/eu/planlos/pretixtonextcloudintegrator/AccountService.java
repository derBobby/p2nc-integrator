package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.common.notification.SignalService;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.AccountCreationException;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.OrderDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixApiOrderService;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.IWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.common.notification.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Core class of the application. Has callback interface for Pretix package and runs request against Nextcloud API
 */
@Slf4j
@Service
public class AccountService implements IWebHookHandler {

    public static final String SUBJECT_OK = "Account creation successful";
    public static final String SUBJECT_FAIL = "Account creation failed";

    private static final String ORDER_APPROVED = "pretix.event.order.approved";
    private static final String ORDER_NEED_APPROVAL = "pretix.event.order.placed.require_approval";

    private final PretixApiOrderService pretixApiOrderService;
    private final NextcloudApiUserService nextcloudApiUserService;
    private final MailService mailService;
    private final SignalService signalService;

    public AccountService(PretixApiOrderService pretixApiOrderService, NextcloudApiUserService nextcloudApiUserService, MailService mailService, SignalService signalService) {
        this.pretixApiOrderService = pretixApiOrderService;
        this.nextcloudApiUserService = nextcloudApiUserService;
        this.mailService = mailService;
        this.signalService = signalService;
    }

    @Override
    public void handle(WebHookDTO webHookDTO) {

        log.info("Incoming webhook: {}", webHookDTO);

        //TODO Add tests for the cases
        if(webHookDTO.action().equals(ORDER_NEED_APPROVAL)) {
            handleApprovalNotification(webHookDTO);
            return;
        }

        if(webHookDTO.action().equals(ORDER_APPROVED)) {
            handleUserCreation(webHookDTO);
            return;
        }

        log.info("Webhook not relevant: {}", webHookDTO);
    }

    private void handleApprovalNotification(WebHookDTO webHookDTO) {
        signalService.notifyAdmin(
                String.join(" ", "New order needs approval! See:", pretixApiOrderService.getEventUrl(webHookDTO.code())));
    }

    private void handleUserCreation(WebHookDTO webHookDTO) {

        // TODO better error handling
        try {

            OrderDTO orderDTO = pretixApiOrderService.fetchOrderFromPretix(webHookDTO.code());
            log.info("Order found: {}", orderDTO);
            Map<String, String> userMap = nextcloudApiUserService.getAllUsersAsUseridEmailMap();

            failIfAddressAlreadyInUse(orderDTO.getEmail(), userMap);

            // Create user
            String userid = generateUserId(userMap, orderDTO.getFirstName(), orderDTO.getLastName());
            nextcloudApiUserService.createUser(userid, orderDTO.getEmail(), orderDTO.getFirstName(), orderDTO.getLastName());
            String successMessage = String.format("Account %s / %s successfully created", userid, orderDTO.getEmail());
            notifyAdmin(SUBJECT_OK, successMessage);
            log.info(successMessage);

        } catch (AccountCreationException | ApiException e) {
            String errorMessage = String.format("Error creating account for order code %s: %s", webHookDTO.code(), e.getMessage());
            log.error(errorMessage);
            notifyAdmin(SUBJECT_FAIL, e.getMessage());
        }
    }

    // TODO Move the two calls into seprate Service that knows all notification services?
    private void notifyAdmin(String subject, String successMessage) {
        mailService.notifyAdmin(subject, successMessage);
        signalService.notifyAdmin(subject, successMessage);
    }

    private void failIfAddressAlreadyInUse(String email, Map<String, String> userMap) {
        boolean emailAlreadyInUse = userMap.containsValue(email);
        if (emailAlreadyInUse) {
            throw new AccountCreationException("Email address is already in use");
        }
        log.info("Email address is still free, proceeding");
    }

    private String generateUserId(Map<String, String> userMap, String firstName, String lastName) {
        return generateUserId(userMap, firstName, lastName, 1);
    }

    private String generateUserId(Map<String, String> userMap, String firstName, String lastName, int charCount) {

        // Assert because <= 0 can only happen for coding errors
        assert charCount > 0;

        if (charCount > firstName.length()) {
            throw new AccountCreationException("No free userid can be generated");
        }

        String userid = String.format(
                "kv-kraichgau-%s%s",
                firstName.substring(0, charCount).toLowerCase(),
                lastName.toLowerCase());

        if (userMap.containsKey(userid)) {
            log.info("Minimal userid is already in use: {}", userid);
            return generateUserId(userMap, firstName, lastName, charCount + 1);
        }

        log.info("Created userid is {}", userid);
        return userid;
    }
}