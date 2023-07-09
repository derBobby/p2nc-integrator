package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.common.notification.MailService;
import eu.planlos.pretixtonextcloudintegrator.common.notification.SignalService;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.AccountCreationException;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.pretix.IWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Booking;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Position;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
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

    public void handleApprovalNotification(String code) {
        notifyAdmin("New order",
                String.join(" ", "New order needs approval! See:", pretixApiOrderService.getEventUrl(code)));
    }

    public void handleUserCreation(WebHookDTO webHookDTO) {

        // TODO better error handling
        try {

            Booking booking = bookingService.loadOrFetch(webHookDTO.code());
            log.info("Order found: {}", booking);

            if(irrelevantForBooking(booking)) {
                String infoMessage = String.format("Order with code %s was excluded by filter for account creation", webHookDTO.code());
                log.info(infoMessage);
                notifyAdmin(SUBJECT_IRRELEVANT, infoMessage);
                return;
            }

            String userid = nextcloudApiUserService.createUser(booking.getEmail(), booking.getFirstname(), booking.getLastname());
            String successMessage = String.format("Account %s / %s successfully created", userid, booking.getEmail());
            notifyAdmin(SUBJECT_OK, successMessage);
            log.info(successMessage);

        } catch (AccountCreationException | ApiException e) {
            String errorMessage = String.format("Error creating account for order code %s: %s", webHookDTO.code(), e.getMessage());
            log.error(errorMessage);
            notifyAdmin(SUBJECT_FAIL, e.getMessage());
        }
    }

    private boolean irrelevantForBooking(Booking booking) {
        List<Position> ticketPositionList = booking.getPositionList().stream()
                .filter(p -> ! p.getProduct().getProductType().isAddon())
                .filter(p -> ! p.getQnA().isEmpty())
                .filter(p -> qnaFilterService.filter(p.getQnA()))
                .toList();
        return ticketPositionList.isEmpty();
    }

    // TODO Move the two calls into seprate Service that knows all notification services?
    private void notifyAdmin(String subject, String successMessage) {
        mailService.notifyAdmin(subject, successMessage);
        signalService.notifyAdmin(subject, successMessage);
    }
}