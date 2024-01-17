package eu.planlos.p2ncintegrator;

import eu.planlos.javamailconnector.MailService;
import eu.planlos.javanextcloudconnector.service.NextcloudApiUserService;
import eu.planlos.javapretixconnector.model.Booking;
import eu.planlos.javapretixconnector.service.PretixBookingService;
import eu.planlos.javapretixconnector.service.PretixEventFilterService;
import eu.planlos.javapretixconnector.service.api.PretixApiOrderService;
import eu.planlos.javasignalconnector.SignalService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.planlos.javapretixconnector.model.dto.PretixSupportedActions.ORDER_APPROVED;
import static eu.planlos.javapretixconnector.model.dto.PretixSupportedActions.ORDER_NEED_APPROVAL;
import static eu.planlos.p2ncintegrator.AccountService.SUBJECT_IRRELEVANT;
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

    private final static String HOOK_ORGANIZER = "organizer";
    private final static String HOOK_EVENT = "event";
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
        accountService.handleWebhook(HOOK_ORGANIZER, HOOK_EVENT, HOOK_CODE, ORDER_NEED_APPROVAL);

        // Check
        verify(mailService).sendMailToRecipients(anyString(), matches(String.format(".*%s.*", HOOK_CODE)));
        verify(signalService).sendMessageToRecipients(anyString());
    }

    /**
     * Order approved tests
     */
    @Test
    public void irrelevantForBooking_noAccountCreated() {
        // Prepare
        //      objects
        //      methods
        when(pretixBookingService.loadOrFetch(HOOK_ORGANIZER, HOOK_EVENT, HOOK_CODE)).thenReturn(mockBooking);
        positionFilterIrrelevant();

        // Act
        accountService.handleWebhook(HOOK_ORGANIZER, HOOK_EVENT, HOOK_CODE, ORDER_APPROVED);

        // Check
        verifyNoInteractions(nextcloudApiUserService);
        verify(mailService).sendMailToAdmin(eq(SUBJECT_IRRELEVANT), anyString());
        verify(signalService).sendMessageToAdmin(contains(SUBJECT_IRRELEVANT));
    }

    @Test
    public void relevantForBooking_accountCreated() {
        // Prepare
        //      objects
        //      methods
        when(pretixBookingService.loadOrFetch(HOOK_ORGANIZER, HOOK_EVENT, HOOK_CODE)).thenReturn(mockBooking);
        positionFilterRelevant();

        // Act
        accountService.handleWebhook(HOOK_ORGANIZER, HOOK_EVENT, HOOK_CODE, ORDER_APPROVED);

        // Check
        verify(nextcloudApiUserService).createUser(anyString(), anyString(), anyString());
        verify(mailService).sendMailToRecipients(eq(SUBJECT_IRRELEVANT), anyString());
        verify(signalService).sendMessageToRecipients(contains(SUBJECT_IRRELEVANT));
    }

    private void positionFilterIrrelevant() {
        when(pretixEventFilterService.bookingNotWantedByAnyFilter(any(), any())).thenReturn(true);
    }

    private void positionFilterRelevant() {
        when(pretixEventFilterService.bookingNotWantedByAnyFilter(any(), any())).thenReturn(false);
    }
}