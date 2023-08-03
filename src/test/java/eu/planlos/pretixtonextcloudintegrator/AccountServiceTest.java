package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.notification.MailService;
import eu.planlos.pretixtonextcloudintegrator.common.notification.SignalService;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.pretix.PretixTestDataUtility;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Booking;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixBookingService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixQnaFilterService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.planlos.pretixtonextcloudintegrator.AccountService.SUBJECT_IRRELEVANT;
import static eu.planlos.pretixtonextcloudintegrator.AccountService.SUBJECT_OK;
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
    PretixQnaFilterService pretixQnaFilterService;

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
        String hookCode = hook.code();
        String hookEvent = hook.event();
        //      methods
        when(pretixApiOrderService.getEventUrl(hookEvent, hookCode)).thenReturn(String.format("https://example.com/%s", hookCode));

        // Act
        accountService.handleApprovalNotification(hookEvent, hookCode);

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
        Booking booking = booking();
        //      methods
        when(pretixBookingService.loadOrFetch(hook.event(), hook.code())).thenReturn(booking);
        positionFilterIrrelevant();

        // Act
        accountService.handleUserCreation(hook.event(), hook.code());

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
        Booking booking = booking();
        //      methods
        when(pretixBookingService.loadOrFetch(hook.event(), hook.code())).thenReturn(booking);
        positionFilterRelevant();

        // Act
        accountService.handleUserCreation(hook.event(), hook.code());

        // Check
        verify(nextcloudApiUserService).createUser(booking.getEmail(), booking.getFirstname(), booking.getLastname());
        verify(mailService).notifyAdmin(eq(SUBJECT_OK), anyString());
        verify(signalService).notifyAdmin(eq(SUBJECT_OK), anyString());
    }

    private void positionFilterIrrelevant() {
        when(pretixQnaFilterService.irrelevantForBooking(any())).thenReturn(true);
    }

    private void positionFilterRelevant() {
        when(pretixQnaFilterService.irrelevantForBooking(any())).thenReturn(false);
    }
}