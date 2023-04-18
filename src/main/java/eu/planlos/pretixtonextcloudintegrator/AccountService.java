package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.ocs.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.api.pretix.model.OrderDTO;
import eu.planlos.pretixtonextcloudintegrator.api.pretix.service.PretixApiOrderService;
import eu.planlos.pretixtonextcloudintegrator.api.pretix.webhook.model.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.api.pretix.webhook.service.IWebHookHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Core class of the application. Has callback interface for Pretix package and runs request against Nextcloud API
 */
@Slf4j
@Service
public class AccountService implements IWebHookHandler {

    private final PretixApiOrderService pretixApiOrderService;
    private final NextcloudApiUserService nextcloudApiUserService;

    public AccountService(PretixApiOrderService pretixApiOrderService, NextcloudApiUserService nextcloudApiUserService) {
        this.pretixApiOrderService = pretixApiOrderService;
        this.nextcloudApiUserService = nextcloudApiUserService;
    }

    @Override
    public void handle(WebHookDTO webHookDTO) {

        // Call Pretix API
        OrderDTO orderDTO = pretixApiOrderService.queryOrder(webHookDTO.code());

        // Call Nextcloud API
        List<String> usernameList = nextcloudApiUserService.getAllUsernames();

        List<String> userEmailList = usernameList.stream().map(nextcloudApiUserService::getUserEmail).toList();

        boolean found = userEmailList.stream().anyMatch(userEmail -> userEmail.equals(orderDTO.getEmail()));
        if(found) {
            log.info("Email address already in use -> nothing to do for WebHook {}", webHookDTO.code());
            return;
        }

        log.info("Email address is still free -> creating user for WebHook {}", webHookDTO.code());
        String userid = nextcloudApiUserService.createUser(orderDTO.getEmail(), orderDTO.getFirstName(), orderDTO.getLastName());
        //TODO Create Logging?
        log.info("User creation successful. Username: {}", userid);
   }
}
