package eu.planlos.p2ncintegrator;

import eu.planlos.javamailconnector.MailService;
import eu.planlos.javanextcloudconnector.service.NextcloudApiUserService;
import eu.planlos.javapretixconnector.IPretixWebHookHandler;
import eu.planlos.javapretixconnector.model.Booking;
import eu.planlos.javapretixconnector.model.dto.PretixSupportedActions;
import eu.planlos.javapretixconnector.model.dto.WebHookResult;
import eu.planlos.javapretixconnector.service.PretixBookingService;
import eu.planlos.javapretixconnector.service.PretixEventFilterService;
import eu.planlos.javapretixconnector.service.api.PretixApiOrderService;
import eu.planlos.javasignalconnector.SignalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static eu.planlos.javapretixconnector.model.dto.PretixSupportedActions.ORDER_APPROVED;
import static eu.planlos.javapretixconnector.model.dto.PretixSupportedActions.ORDER_NEED_APPROVAL;

/**
 * Core class of the application. Has callback interface for Pretix package and runs request against Nextcloud API
 */
@Slf4j
@Service
public class AccountService implements IPretixWebHookHandler {

    public static final String SUBJECT_OK = "Account creation successful";
    public static final String SUBJECT_FAIL = "Account creation failed";
    public static final String SUBJECT_IRRELEVANT = "Account creation not required";

    private final PretixBookingService pretixBookingService;
    private final PretixEventFilterService pretixEventFilterService;
    private final PretixApiOrderService pretixApiOrderService;
    private final NextcloudApiUserService nextcloudApiUserService;
    private final MailService mailService;
    private final SignalService signalService;

    public AccountService(PretixEventFilterService pretixEventFilterService, PretixBookingService pretixBookingService, PretixApiOrderService pretixApiOrderService, NextcloudApiUserService nextcloudApiUserService, MailService mailService, SignalService signalService) {
        this.pretixEventFilterService = pretixEventFilterService;
        this.pretixBookingService = pretixBookingService;
        this.pretixApiOrderService = pretixApiOrderService;
        this.nextcloudApiUserService = nextcloudApiUserService;
        this.mailService = mailService;
        this.signalService = signalService;
    }

    @Override
    public WebHookResult handleWebhook(String organizer, String event, String code, PretixSupportedActions action) {

        if (action.equals(ORDER_NEED_APPROVAL)) {
            return handleApprovalNotification(event, code);
        }

        if (action.equals(ORDER_APPROVED)) {
            return handleUserCreation(organizer, event, code, action);
        }

        throw new IllegalArgumentException("Unsupported action");
    }

    private WebHookResult handleApprovalNotification(String event, String code) {
        notifyAdmin("New order",
                String.format("New order needs approval! See Pretix: %s", pretixApiOrderService.getEventUrl(event, code)));
        return new WebHookResult(true, "Notification has been sent.");
    }

    private WebHookResult handleUserCreation(String organizer, String event, String code, PretixSupportedActions action) {

        try {
            Booking booking = pretixBookingService.loadOrFetch(organizer, event, code);
            log.info("Order found: {}", booking);

            //TODO IT test
            if(pretixEventFilterService.bookingNotWantedByAnyFilter(action, booking)) {
                String filteredMessage = String.format("Order with code %s was excluded for account creation by filter", code);
                notifyAdmin(SUBJECT_IRRELEVANT, filteredMessage);

                log.info(filteredMessage);
                return new WebHookResult(true, filteredMessage);
            }

            String userid = nextcloudApiUserService.createUser(booking.getEmail(), booking.getFirstname(), booking.getLastname());
            String successMessage = String.format("Account %s / %s successfully created", userid, booking.getEmail());
            notifyAdmin(SUBJECT_OK, successMessage);

            log.info(successMessage);
            return new WebHookResult(true, successMessage);

        } catch (Exception e) {
            String errorMessage = String.format("Error creating account for order code %s: %s", code, e.getMessage());
            log.error(errorMessage);
            notifyAdmin(SUBJECT_FAIL, errorMessage);
            return new WebHookResult(false, errorMessage);
        }
    }

    private void notifyAdmin(String subject, String successMessage) {
        mailService.sendMailToAdmin(subject, successMessage);
        signalService.sendMessageToAdmin(String.format("%s - %s", subject, successMessage));
    }
}