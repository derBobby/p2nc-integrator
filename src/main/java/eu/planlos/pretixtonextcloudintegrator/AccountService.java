package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.notification.MailService;
import eu.planlos.pretixtonextcloudintegrator.common.notification.SignalService;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.pretix.IPretixWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Booking;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixBookingService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixEventFilterService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public void handleApprovalNotification(String hookAction, String event, String code) {
        notifyAdmin("New order",
                String.join(" ", "New order needs approval! See:", pretixApiOrderService.getEventUrl(event, code)));
    }

    public void handleUserCreation(String action, String event, String code) {

        try {
            Booking booking = pretixBookingService.loadOrFetch(event, code);
            log.info("Order found: {}", booking);

            if(pretixEventFilterService.irrelevantForBooking(action, booking)) {
                String infoMessage = String.format("Order with code %s was excluded for account creation by filter", code);
                log.info(infoMessage);
                notifyAdmin(SUBJECT_IRRELEVANT, infoMessage);
                return;
            }

            String userid = nextcloudApiUserService.createUser(booking.getEmail(), booking.getFirstname(), booking.getLastname());
            String successMessage = String.format("Account %s / %s successfully created", userid, booking.getEmail());
            notifyAdmin(SUBJECT_OK, successMessage);
            log.info(successMessage);

        } catch (Exception e) {
            String errorMessage = String.format("Error creating account for order code %s: %s", code, e.getMessage());
            log.error(errorMessage);
            notifyAdmin(SUBJECT_FAIL, errorMessage);
        }
    }

    private void notifyAdmin(String subject, String successMessage) {
        mailService.notifyAdmin(subject, successMessage);
        signalService.notifyAdmin(subject, successMessage);
    }
}