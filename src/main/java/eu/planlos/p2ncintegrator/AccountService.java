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

import java.util.Optional;

import static eu.planlos.javapretixconnector.model.dto.PretixSupportedActions.ORDER_APPROVED;
import static eu.planlos.javapretixconnector.model.dto.PretixSupportedActions.ORDER_NEED_APPROVAL;

/**
 * Core class of the application. Has callback interface for Pretix package and runs request against Nextcloud API
 */
@Slf4j
@Service
public class AccountService implements IPretixWebHookHandler {

    public static final String MESSAGE_OK = "Nextcloud account creation successful ✅";
    public static final String MESSAGE_FAIL = "Nextcloud account creation failed ❌";
    public static final String MESSAGE_IRRELEVANT = "Nextcloud account not required";

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

        Booking booking = pretixBookingService.loadOrFetch(organizer, event, code);
        log.info("Order found: {}", booking);

        if(pretixEventFilterService.bookingNotWantedByAnyFilter(action, booking)) {
            String filteredMessage = String.format("%s: Order was excluded for account creation by filter", MESSAGE_IRRELEVANT);
            notifyAdmin(booking.getCode(), filteredMessage);

            log.info(filteredMessage);
            return new WebHookResult(true, filteredMessage);
        }

        if (action.equals(ORDER_NEED_APPROVAL)) {
            return handleApprovalNotification(booking);
        }

        if (action.equals(ORDER_APPROVED)) {
            return handleUserCreation(booking);
        }

        throw new IllegalArgumentException("Unsupported action");
    }

    private WebHookResult handleApprovalNotification(Booking booking) {

        String url = pretixApiOrderService.getEventUrl(booking.getEvent(), booking.getCode());

        String message = String.format("New order of '%s %s' / %s needs approval! See Pretix: %s", booking.getFirstname(), booking.getLastname(), booking.getEmail(), url);
        notifyRecipients(booking.getCode(), message);
        return new WebHookResult(true, "Notification has been sent.");
    }

    private WebHookResult handleUserCreation(Booking booking) {

        try {
            Optional<String> userid = nextcloudApiUserService.createUser(booking.getEmail(), booking.getFirstname(), booking.getLastname());

            String message = String.format("%s: Mail address (%s) already in use", MESSAGE_IRRELEVANT, booking.getEmail());
            if(userid.isPresent()) {
                message = String.format("%s: %s / %s", MESSAGE_OK, userid.get(), booking.getEmail());
            }

            notifyRecipients(booking.getCode(), message);
            log.info(message);
            return new WebHookResult(true, message);

        } catch (Exception e) {
            String errorMessage = String.format("Error creating account: %s", e.getMessage());
            log.error(errorMessage);
            notifyRecipients(MESSAGE_FAIL, errorMessage);
            return new WebHookResult(false, errorMessage);
        }
    }

    private void notifyAdmin(String code, String message) {
        String subject = subject(code);
        mailService.sendMailToAdmin(subject, message);
        signalService.sendMessageToAdmin(String.format("%s - %s", subject, message));
    }

    private void notifyRecipients(String code, String message) {
        String subject = subject(code);
        mailService.sendMailToRecipients(subject, message);
        signalService.sendMessageToRecipients(String.format("%s - %s", subject, message));
    }

    private String subject(String code) {
        return String.format("Order %s", code);
    }
}