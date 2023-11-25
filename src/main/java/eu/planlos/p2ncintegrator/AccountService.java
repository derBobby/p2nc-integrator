package eu.planlos.p2ncintegrator;

import eu.planlos.javanextcloudconnector.service.NextcloudApiUserService;
import eu.planlos.p2ncintegrator.common.notification.MailService;
import eu.planlos.p2ncintegrator.common.notification.SignalService;
import eu.planlos.p2ncintegrator.pretix.IPretixWebHookHandler;
import eu.planlos.p2ncintegrator.pretix.model.Booking;
import eu.planlos.p2ncintegrator.pretix.service.PretixBookingService;
import eu.planlos.p2ncintegrator.pretix.service.PretixEventFilterService;
import eu.planlos.p2ncintegrator.pretix.service.api.PretixApiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public void handleApprovalNotification(String action, String event, String code) {
        notifyAdmin("New order",
                String.join(" ", "New order needs approval! See:", pretixApiOrderService.getEventUrl(event, code)));
    }

    @Override
    public Optional<String> handleUserCreation(String action, String event, String code) {

        try {
            Booking booking = pretixBookingService.loadOrFetch(event, code);
            log.info("Order found: {}", booking);

            //TODO IT test
            if(pretixEventFilterService.bookingNotWantedByAnyFilter(action, booking)) {
                String infoMessage = String.format("Order with code %s was excluded for account creation by filter", code);
                log.info(infoMessage);
                notifyAdmin(SUBJECT_IRRELEVANT, infoMessage);
                return Optional.empty();
            }

            String userid = nextcloudApiUserService.createUser(booking.getEmail(), booking.getFirstname(), booking.getLastname());
            String successMessage = String.format("Account %s / %s successfully created", userid, booking.getEmail());
            notifyAdmin(SUBJECT_OK, successMessage);
            log.info(successMessage);

            return Optional.of(successMessage);

        } catch (Exception e) {
            String errorMessage = String.format("Error creating account for order code %s: %s", code, e.getMessage());
            log.error(errorMessage);
            notifyAdmin(SUBJECT_FAIL, errorMessage);
            return Optional.empty();
        }
    }

    private void notifyAdmin(String subject, String successMessage) {
        mailService.notifyAdmin(subject, successMessage);
        signalService.notifyAdmin(subject, successMessage);
    }
}