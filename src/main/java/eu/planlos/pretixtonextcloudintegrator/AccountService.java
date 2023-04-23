package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.exception.AccountCreationException;
import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.ocs.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.api.pretix.model.OrderDTO;
import eu.planlos.pretixtonextcloudintegrator.api.pretix.service.PretixApiOrderService;
import eu.planlos.pretixtonextcloudintegrator.api.pretix.webhook.model.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.api.pretix.webhook.service.IWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.common.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Core class of the application. Has callback interface for Pretix package and runs request against Nextcloud API
 */
@Slf4j
@Service
public class AccountService implements IWebHookHandler {

    private final PretixApiOrderService pretixApiOrderService;
    private final NextcloudApiUserService nextcloudApiUserService;
    private final MailService mailService;

    public AccountService(PretixApiOrderService pretixApiOrderService, NextcloudApiUserService nextcloudApiUserService, MailService mailService) {
        this.pretixApiOrderService = pretixApiOrderService;
        this.nextcloudApiUserService = nextcloudApiUserService;
        this.mailService = mailService;
    }

    @Override
    public void handle(WebHookDTO webHookDTO) {

        // Call Pretix API to get Order
        OrderDTO orderDTO = pretixApiOrderService.queryOrder(webHookDTO.code());

        // Call Nextcloud API to get full list of usernames
        List<String> usernameList = nextcloudApiUserService.getAllUsernames();

        // Call Nextcloud API to get all users
        Map<String, String> userMap = usernameList.stream()
                .map(nextcloudApiUserService::getUserMap)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Check if mail address is already in use
        boolean emailAlreadyInUse = userMap.containsValue(orderDTO.getEmail());
        if(emailAlreadyInUse) {
            log.info("Email address already in use -> nothing to do for WebHook {}", webHookDTO.code());
            // TODO send info that mail address is already in use
            return;
        }
        log.info("Email address is still free. Proceeding for WebHook {}", webHookDTO.code());

        try {
            // Find username to create account
            String userid = generateUserId(userMap, orderDTO.getFirstName(), orderDTO.getLastName());
            // Call Nextcloud API to create new user
            nextcloudApiUserService.createUser(userid, orderDTO.getEmail(), orderDTO.getFirstName(), orderDTO.getLastName());

            log.info("User {} created successfully for WebHook {} ", userid, webHookDTO.code());
        } catch (AccountCreationException e) {
            log.error(e.getMessage());
            mailService.notifyAdminOfException(
                    "Account creation failed",
                    String.format(
                            "order code=%s, firstname=%s, lastname=%s",
                            orderDTO.getCode(),
                            orderDTO.getFirstName(),
                            orderDTO.getLastName()),
                    e.getMessage());
        }

   }

    private String generateUserId(Map<String, String> userMap, String firstName, String lastName) throws AccountCreationException {
        return generateUserId(userMap, firstName, lastName, 1);
    }

    private String generateUserId(Map<String, String> userMap, String firstName, String lastName, int charCount) throws AccountCreationException {

        // Assert because <= 0 can only happen for coding errors
        assert charCount > 0;

        if(charCount > firstName.length()) {
            throw new AccountCreationException("Existing userid can't be avoided: Duplicate booking or similar first name?");
        }

        String userid = String.format(
                "kv-kraichgau-%s%s",
                firstName.substring(0, charCount).toLowerCase(),
                lastName.toLowerCase());

        if (userMap.containsKey(userid)) {
            log.info("Minimal userid is already in use: {}", userid);
            return generateUserId(userMap, firstName, lastName, charCount+1);
        }

        log.info("Created userid is {}", userid);
        return userid;
    }

}
