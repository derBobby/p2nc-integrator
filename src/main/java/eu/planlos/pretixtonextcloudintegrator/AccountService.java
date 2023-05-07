package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.AccountCreationException;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.OrderDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixApiOrderService;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.IWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.common.mail.MailService;
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

        log.info("Incoming webhook: {}", webHookDTO);

        // TODO better error handling
        OrderDTO orderDTO = null;

        try {
            orderDTO = pretixApiOrderService.fetchOrderFromPretix(webHookDTO.code());
            log.info("Order found: {}", orderDTO);
            Map<String, String> userMap = nextcloudApiUserService.getAllUsersAsUseridEmailMap();

            failIfAddressAlreadyInUse(orderDTO, userMap);

            // Create user
            String userid = generateUserId(userMap, orderDTO.getFirstName(), orderDTO.getLastName());
            nextcloudApiUserService.createUser(userid, orderDTO.getEmail(), orderDTO.getFirstName(), orderDTO.getLastName());
            mailService.notifyAdmin(SUBJECT_OK, "Account successfully created.");
            log.info("Account {} successfully created", userid);

        } catch (AccountCreationException | ApiException e) {
            log.error(e.getMessage());
            mailService.notifyAdmin(SUBJECT_FAIL, e.getMessage());
        }
    }

    private void failIfAddressAlreadyInUse(OrderDTO orderDTO, Map<String, String> userMap) {
        boolean emailAlreadyInUse = userMap.containsValue(orderDTO.getEmail());
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
