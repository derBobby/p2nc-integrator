package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.notification.MailService;
import eu.planlos.pretixtonextcloudintegrator.common.notification.SignalService;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Booking;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.BookingService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static eu.planlos.pretixtonextcloudintegrator.AccountService.SUBJECT_IRRELEVANT;
import static eu.planlos.pretixtonextcloudintegrator.AccountService.SUBJECT_OK;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest extends TestGenerator {

    @Mock
    BookingService bookingService;

    @Mock
    PretixApiOrderService pretixApiOrderService;

    @Mock
    NextcloudApiUserService nextcloudApiUserService;

    @Mock
    QnaFilterService qnaFilterService;

    @Mock
    MailService mailService;

    @Mock
    SignalService signalService;

    @InjectMocks
    AccountService accountService;

    /**
     * New Order tests
     */
    @Test
    public void orderApprovalRequired_adminIsNotified() {
        // Prepare
        //      objects
        String orderCode = newCode();
        //      methods
        when(pretixApiOrderService.getEventUrl(orderCode)).thenReturn(String.format("https://example.com/%s", orderCode));

        // Act
        accountService.handleApprovalNotification(orderCode);

        // Check
        verify(mailService).notifyAdmin(anyString(), matches(String.format(".*%s.*", orderCode)));
        verify(signalService).notifyAdmin(anyString(), matches(String.format(".*%s.*", orderCode)));
    }

    /**
     * Order approved tests
     */
    @Test
    public void irrelevantForBooking_noAccountCreated() {
        // Prepare
        //      objects
        WebHookDTO webHook = orderApprovedHook();
        Booking booking = booking();
        //      methods
        when(bookingService.loadOrFetch(webHook.code())).thenReturn(booking);
        positionFilterIrrelevant();

        // Act
        accountService.handleUserCreation(webHook);

        // Check
        verifyNoInteractions(nextcloudApiUserService);
        verify(mailService).notifyAdmin(eq(SUBJECT_IRRELEVANT), anyString());
        verify(signalService).notifyAdmin(eq(SUBJECT_IRRELEVANT), anyString());
    }

    @Test
    public void relevantForBooking_accountCreated() {
        // Prepare
        //      objects
        WebHookDTO webHook = orderApprovedHook();
        Booking booking = booking();
        //      methods
        when(bookingService.loadOrFetch(webHook.code())).thenReturn(booking);
        positionFilterRelevant();

        // Act
        accountService.handleUserCreation(webHook);

        // Check
        verify(nextcloudApiUserService).createUser(booking.getEmail(), booking.getFirstname(), booking.getLastname());
        verify(mailService).notifyAdmin(eq(SUBJECT_OK), anyString());
        verify(signalService).notifyAdmin(eq(SUBJECT_OK), anyString());
    }

    private void positionFilterDisabled() {
        positionFilterRelevant();
    }

    private void positionFilterIrrelevant() {
        when(qnaFilterService.filter(anyMap())).thenReturn(false);
    }

    private void positionFilterRelevant() {
        when(qnaFilterService.filter(anyMap())).thenReturn(true);
    }
}