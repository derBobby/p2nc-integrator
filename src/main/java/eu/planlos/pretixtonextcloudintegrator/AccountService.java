package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.notification.MailService;
import eu.planlos.pretixtonextcloudintegrator.common.notification.SignalService;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.pretix.IWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Booking;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Position;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.BookingService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Core class of the application. Has callback interface for Pretix package and runs request against Nextcloud API
 */
@Slf4j
@Service
public class AccountService implements IWebHookHandler {

    public static final String SUBJECT_OK = "Account creation successful";
    public static final String SUBJECT_FAIL = "Account creation failed";
    public static final String SUBJECT_IRRELEVANT = "Account creation not required";

    private final BookingService bookingService;
    private final QnaFilterService qnaFilterService;
    private final PretixApiOrderService pretixApiOrderService;
    private final NextcloudApiUserService nextcloudApiUserService;
    private final MailService mailService;
    private final SignalService signalService;

    public AccountService(QnaFilterService qnaFilterService, BookingService bookingService, PretixApiOrderService pretixApiOrderService, NextcloudApiUserService nextcloudApiUserService, MailService mailService, SignalService signalService) {
        this.qnaFilterService = qnaFilterService;
        this.bookingService = bookingService;
        this.pretixApiOrderService = pretixApiOrderService;
        this.nextcloudApiUserService = nextcloudApiUserService;
        this.mailService = mailService;
        this.signalService = signalService;
    }

    //TODO event from context or via parameter`?
    public void handleApprovalNotification(String event, String code) {
        notifyAdmin("New order",
                String.join(" ", "New order needs approval! See:", pretixApiOrderService.getEventUrl(code)));
    }

    public void handleUserCreation(String event, String code) {

        try {
            Booking booking = bookingService.loadOrFetch(code);
            log.info("Order found: {}", booking);

            if(irrelevantForBooking(event, booking)) {
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

    private boolean irrelevantForBooking(String event, Booking booking) {
        List<Position> ticketPositionList = booking.getPositionList().stream()
                .filter(p -> ! p.getProduct().getProductType().isAddon())
                .filter(p -> ! p.getQnA().isEmpty())
                .filter(p -> qnaFilterService.filter(event, p.getQnA()))
                .toList();
        return ticketPositionList.isEmpty();
    }

    private void notifyAdmin(String subject, String successMessage) {
        mailService.notifyAdmin(subject, successMessage);
        signalService.notifyAdmin(subject, successMessage);
    }
}