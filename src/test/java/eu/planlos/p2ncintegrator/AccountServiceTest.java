package eu.planlos.p2ncintegrator;

import eu.planlos.javanextcloudconnector.service.NextcloudApiUserService;
import eu.planlos.javapretixconnector.model.Booking;
import eu.planlos.javapretixconnector.service.PretixBookingService;
import eu.planlos.javapretixconnector.service.PretixEventFilterService;
import eu.planlos.javapretixconnector.service.api.PretixApiOrderService;
import eu.planlos.p2ncintegrator.common.notification.MailService;
import eu.planlos.p2ncintegrator.common.notification.SignalService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.planlos.javapretixconnector.model.dto.PretixSupportedActions.*;
import static eu.planlos.p2ncintegrator.AccountService.SUBJECT_IRRELEVANT;
import static eu.planlos.p2ncintegrator.AccountService.SUBJECT_OK;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

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

    private final static Booking mockBooking = mock(Booking.class);

    private final static String HOOK_EVENT = "Event";
    private final static String HOOK_CODE = "XCODE";
    private final static String EMAIL = "email@example.com";
    private final static String FIRSTNAME = "Firstname";
    private final static String LASTNAME = "Lastname";

    @BeforeAll
    public static void mockBooking() {
        when(mockBooking.getEmail()).thenReturn(EMAIL);
        when(mockBooking.getFirstname()).thenReturn(FIRSTNAME);
        when(mockBooking.getLastname()).thenReturn(LASTNAME);
    }

    /**
     * New Order tests
     */
    @Test
    public void orderApprovalRequired_adminIsNotified() {
        // Prepare
        //      objects
        //      methods
        when(pretixApiOrderService.getEventUrl(HOOK_EVENT, HOOK_CODE)).thenReturn(String.format("https://example.com/%s", HOOK_CODE));

        // Act
        accountService.handleWebhook(ORDER_NEED_APPROVAL, HOOK_EVENT, HOOK_CODE);

        // Check
        verify(mailService).notifyAdmin(anyString(), matches(String.format(".*%s.*", HOOK_CODE)));
        verify(signalService).notifyAdmin(anyString(), matches(String.format(".*%s.*", HOOK_CODE)));
    }

    /**
     * Order approved tests
     */
    @Test
    public void irrelevantForBooking_noAccountCreated() {
        // Prepare
        //      objects
        //      methods
        when(pretixBookingService.loadOrFetch(HOOK_EVENT, HOOK_CODE)).thenReturn(mockBooking);
        positionFilterIrrelevant();

        // Act
        accountService.handleWebhook(ORDER_APPROVED, HOOK_EVENT, HOOK_CODE);

        // Check
        verifyNoInteractions(nextcloudApiUserService);
        verify(mailService).notifyAdmin(eq(SUBJECT_IRRELEVANT), anyString());
        verify(signalService).notifyAdmin(eq(SUBJECT_IRRELEVANT), anyString());
    }

    @Test
    public void relevantForBooking_accountCreated() {
        // Prepare
        //      objects
        //      methods
        when(pretixBookingService.loadOrFetch(HOOK_EVENT, HOOK_CODE)).thenReturn(mockBooking);
        positionFilterRelevant();

        // Act
        accountService.handleWebhook(ORDER_APPROVED, HOOK_EVENT, HOOK_CODE);

        // Check
        verify(nextcloudApiUserService).createUser(anyString(), anyString(), anyString());
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