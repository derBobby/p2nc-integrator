package eu.planlos.p2ncintegrator;

import eu.planlos.javanextcloudconnector.service.NextcloudApiUserService;
import eu.planlos.p2ncintegrator.common.notification.MailService;
import eu.planlos.p2ncintegrator.common.notification.SignalService;
import eu.planlos.p2ncintegrator.pretix.PretixTestDataUtility;
import eu.planlos.p2ncintegrator.pretix.model.Booking;
import eu.planlos.p2ncintegrator.pretix.model.dto.WebHookDTO;
import eu.planlos.p2ncintegrator.pretix.service.PretixBookingService;
import eu.planlos.p2ncintegrator.pretix.service.PretixEventFilterService;
import eu.planlos.p2ncintegrator.pretix.service.api.PretixApiOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.planlos.p2ncintegrator.AccountService.SUBJECT_IRRELEVANT;
import static eu.planlos.p2ncintegrator.AccountService.SUBJECT_OK;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest extends PretixTestDataUtility {

    @Mock
    PretixBookingService pretixBookingService;

    @Mock
    PretixApiOrderService pretixApiOrderService;

    @Mock
    NextcloudApiUserService nextcloudApiUserService;

    @Mock
    PretixEventFilterService pretixEventFilterService;

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
        WebHookDTO hook = orderApprovedHook();
        String hookAction = hook.action();
        String hookCode = hook.code();
        String hookEvent = hook.event();
        //      methods
        when(pretixApiOrderService.getEventUrl(hookEvent, hookCode)).thenReturn(String.format("https://example.com/%s", hookCode));

        // Act
        accountService.handleApprovalNotification(hookAction, hookEvent, hookCode);

        // Check
        verify(mailService).notifyAdmin(anyString(), matches(String.format(".*%s.*", hookCode)));
        verify(signalService).notifyAdmin(anyString(), matches(String.format(".*%s.*", hookCode)));
    }

    /**
     * Order approved tests
     */
    @Test
    public void irrelevantForBooking_noAccountCreated() {
        // Prepare
        //      objects
        WebHookDTO hook = orderApprovedHook();
        Booking booking = ticketBooking();
        //      methods
        when(pretixBookingService.loadOrFetch(hook.event(), hook.code())).thenReturn(booking);
        positionFilterIrrelevant();

        // Act
        accountService.handleUserCreation(hook.action(), hook.event(), hook.code());

        // Check
        verifyNoInteractions(nextcloudApiUserService);
        verify(mailService).notifyAdmin(eq(SUBJECT_IRRELEVANT), anyString());
        verify(signalService).notifyAdmin(eq(SUBJECT_IRRELEVANT), anyString());
    }

    @Test
    public void relevantForBooking_accountCreated() {
        // Prepare
        //      objects
        WebHookDTO hook = orderApprovedHook();
        Booking booking = ticketBooking();
        //      methods
        when(pretixBookingService.loadOrFetch(hook.event(), hook.code())).thenReturn(booking);
        positionFilterRelevant();

        // Act
        accountService.handleUserCreation(hook.action(), hook.event(), hook.code());

        // Check
        verify(nextcloudApiUserService).createUser(booking.getEmail(), booking.getFirstname(), booking.getLastname());
        verify(mailService).notifyAdmin(eq(SUBJECT_OK), anyString());
        verify(signalService).notifyAdmin(eq(SUBJECT_OK), anyString());
    }

    private void positionFilterIrrelevant() {
        when(pretixEventFilterService.bookingNotWantedByAnyFilter(anyString(), any())).thenReturn(true);
    }

    private void positionFilterRelevant() {
        when(pretixEventFilterService.bookingNotWantedByAnyFilter(anyString(), any())).thenReturn(false);
    }
}