package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.common.notification.MailService;
import eu.planlos.pretixtonextcloudintegrator.common.notification.SignalService;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.AccountCreationException;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.pretix.IWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Order;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.OrderService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiOrderService;
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

    private final OrderService orderService;
    private final PretixApiOrderService pretixApiOrderService;
    private final NextcloudApiUserService nextcloudApiUserService;
    private final MailService mailService;
    private final SignalService signalService;

    public AccountService(OrderService orderService, PretixApiOrderService pretixApiOrderService, NextcloudApiUserService nextcloudApiUserService, MailService mailService, SignalService signalService) {
        this.orderService = orderService;
        this.pretixApiOrderService = pretixApiOrderService;
        this.nextcloudApiUserService = nextcloudApiUserService;
        this.mailService = mailService;
        this.signalService = signalService;
    }

    public void handleApprovalNotification(String code) {
        signalService.notifyAdmin(
                String.join(" ", "New order needs approval! See:", pretixApiOrderService.getEventUrl(code)));
    }

    public void handleUserCreation(WebHookDTO webHookDTO) {

        // TODO better error handling
        try {

            Order order = orderService.getOrderForCode(webHookDTO.code());
            log.info("Order found: {}", order);

            //TODO CONTINUE HERE - CHECK IF BOOKING IS FOR ZELTLAGER OR AUFBAULAGER
            Map<String, String> allUsersMap = nextcloudApiUserService.getAllUsersAsUseridEmailMap();
            failIfAddressAlreadyInUse(order.email(), allUsersMap);

            // Create user
            String userid = generateUserId(allUsersMap, order.firstname(), order.lastname());
            nextcloudApiUserService.createUser(userid, order.email(), order.firstname(), order.lastname());
            String successMessage = String.format("Account %s / %s successfully created", userid, order.email());
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

    //TODO Should be part of NC package, because username is NC Logic
    private String generateUserId(Map<String, String> allUsersMap, String firstName, String lastName) {
        return generateUserId(allUsersMap, firstName, lastName, 1);
    }

    //TODO Should be part of NC package, because username is NC Logic
    private String generateUserId(Map<String, String> allUsersMap, String firstName, String lastName, int charCount) {

        // Assert because <= 0 can only happen for coding errors
        assert charCount > 0;

        if (charCount > firstName.length()) {
            throw new AccountCreationException("No free userid can be generated");
        }

        String userid = String.format(
                "kv-kraichgau-%s%s",
                firstName.substring(0, charCount).toLowerCase(),
                lastName.toLowerCase());

        if (allUsersMap.containsKey(userid)) {
            log.info("Minimal userid is already in use: {}", userid);
            return generateUserId(allUsersMap, firstName, lastName, charCount + 1);
        }

        log.info("Created userid is {}", userid);
        return userid;
    }
}