package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.notification.SignalService;
import eu.planlos.pretixtonextcloudintegrator.common.util.ZonedDateTimeUtility;
import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.*;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.InvoiceAddressDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.NamePartsDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.OrderDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.BookingService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiOrderService;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.common.notification.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

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
     * Account creation tests
     */
    @Test
    public void xxxxxxxxxxxxxxxxxx() {

        // Prepare
        //      existing
        //      order
        final NamePartsDTO namePartsDTO1 = new NamePartsDTO("John", "Doe");
        final InvoiceAddressDTO invoiceAddressDTO1 = new InvoiceAddressDTO(namePartsDTO1.given_name() + " " + namePartsDTO1.family_name(), namePartsDTO1);
        final OrderDTO orderDTO1 = new OrderDTO("c0d3X", invoiceAddressDTO1, "newsuser@example.com", null, null);
        final WebHookDTO webHookDTO = new WebHookDTO(1337L, "organizer", "event", orderDTO1.getCode(), "pretix.event.order.approved");
        //      methods
        when(pretixApiOrderService.fetchOrderFromPretix(orderDTO1.getCode())).thenReturn(orderDTO1);
        when(nextcloudApiUserService.getAllUsersAsUseridEmailMap()).thenReturn(new HashMap<>());

        // Act
        accountService.handleUserCreation(webHookDTO);

        // Check
        verify(nextcloudApiUserService).createUser(anyString(), anyString(), anyString());
    }

    @Test
    public void createAccount_successful() {

        // Prepare
        //      existing
        final String existingUserid1 = "existinguserid1";
        final String existingUserMail1 = "existinguser1@exmaple.com";
        final Booking booking = booking();
        //      methods
        when(bookingService.loadOrFetch(newCode())).thenReturn(booking);
        when(pretixApiOrderService.fetchOrderFromPretix(newCode())).thenReturn(null);
        disablePositionFilter();

        // Act
        accountService.handleUserCreation(orderApprovedHook());

        // Check
        verify(nextcloudApiUserService).createUser(booking.getEmail(), booking.getFirstname(), booking.getLastname());
    }

    @Test
    public void useridGeneration_mailAlreadyInUse() {

        // Prepare
        //      existing
        final String existingUserid1 = "existinguserid1";
        final String existingUserMail1 = "existinguser1@exmaple.com";
        //      order
        final NamePartsDTO namePartsDTO1 = new NamePartsDTO("John", "Doe");
        final InvoiceAddressDTO invoiceAddressDTO1 = new InvoiceAddressDTO(namePartsDTO1.given_name() + " " + namePartsDTO1.family_name(), namePartsDTO1);
        final OrderDTO orderDTO1 = new OrderDTO("c0d3X", invoiceAddressDTO1, existingUserMail1, null, null);
        final WebHookDTO webHookDTO = new WebHookDTO(1337L, "organizer", "event", orderDTO1.getCode(), "pretix.event.order.approved");
        //      methods
        when(pretixApiOrderService.fetchOrderFromPretix(orderDTO1.getCode())).thenReturn(orderDTO1);
        when(nextcloudApiUserService.getAllUsersAsUseridEmailMap()).thenReturn(Map.of(existingUserid1, existingUserMail1));

        // Act
        accountService.handleUserCreation(webHookDTO);

        // Check
        verify(nextcloudApiUserService, times(0)).createUser(anyString(), anyString(), anyString());

    }

    @Test
    public void useridGeneration_useridAlreadyinUse() {

        // Prepare
        //      existing
        final String existingUserid1 = "kv-kraichgau-jdoe";
        final String existingUserMail1 = "existinguser1@exmaple.com";
        //      order
        final NamePartsDTO namePartsDTO1 = new NamePartsDTO("John", "Doe");
        final InvoiceAddressDTO invoiceAddressDTO1 = new InvoiceAddressDTO(namePartsDTO1.given_name() + " " + namePartsDTO1.family_name(), namePartsDTO1);
        final OrderDTO orderDTO1 = new OrderDTO("c0d3X", invoiceAddressDTO1, "newuser1@example.com", null, null);
        final WebHookDTO webHookDTO = new WebHookDTO(1337L, "organizer", "event", orderDTO1.getCode(), "pretix.event.order.approved");
        //      methods
        when(pretixApiOrderService.fetchOrderFromPretix(orderDTO1.getCode())).thenReturn(orderDTO1);
        when(nextcloudApiUserService.getAllUsersAsUseridEmailMap()).thenReturn(Map.of(existingUserid1, existingUserMail1));

        // Act
        accountService.handleUserCreation(webHookDTO);

        // Check
        verify(nextcloudApiUserService).createUser(anyString(), anyString(), anyString());
    }

    @Test
    public void useridGeneration_firstnameNotLongEnoughToAvoidExisting() {

        // Prepare
        //      existing
        final String existingUserid1 = "kv-kraichgau-jdoe";
        final String existingUserMail1 = "existinguser1@exmaple.com";
        //      order
        final NamePartsDTO namePartsDTO1 = new NamePartsDTO("J", "Doe");
        final InvoiceAddressDTO invoiceAddressDTO1 = new InvoiceAddressDTO(namePartsDTO1.given_name() + " " + namePartsDTO1.family_name(), namePartsDTO1);
        final OrderDTO orderDTO1 = new OrderDTO("c0d3X", invoiceAddressDTO1, "newuser1@example.com", null, null);
        final WebHookDTO webHookDTO = new WebHookDTO(1337L, "organizer", "event", orderDTO1.getCode(), "pretix.event.order.approved");
        //      methods
        when(pretixApiOrderService.fetchOrderFromPretix(orderDTO1.getCode())).thenReturn(orderDTO1);
        when(nextcloudApiUserService.getAllUsersAsUseridEmailMap()).thenReturn(Map.of(existingUserid1, existingUserMail1));

        // Act
        accountService.handleUserCreation(webHookDTO);

        // Check
        verify(nextcloudApiUserService, times(0)).createUser(anyString(), anyString(), anyString());
        verify(mailService).notifyAdmin(matches(AccountService.SUBJECT_FAIL), anyString());
        verify(signalService).notifyAdmin(matches(AccountService.SUBJECT_FAIL), anyString());
    }

    /**
     * Generator
     */
    public Booking booking() {
        return new Booking(
                newCode(),
                "First",
                "Last",
                "first.last@example.com",
                ZonedDateTimeUtility.nowCET().toLocalDateTime(),
                positionList());
    }

    private List<Position> positionList() {
        return List.of(new Position(product(), qnaMap()));
    }

    private Product product() {
        return new Product(pretixId(), "some product", productTypeTicket());
    }

    private ProductType productTypeTicket() {
        return new ProductType(pretixId(), false, "some product type");
    }

    private Map<Question, Answer> qnaMap() {
        return Map.of(new Question(pretixId(), "Question?"), new Answer(pretixId(), "Answer!"));
    }

    private PretixId pretixId() {
        return new PretixId(0L);
    }

    private String newCode() {
        return "NC0DE";
    }

    private WebHookDTO orderApprovedHook() {
        return new WebHookDTO(0L, "organizer", "event", newCode(), "pretix.event.order.approved");
    }

    private void disablePositionFilter() {
        when(qnaFilterService.filter(anyMap())).thenReturn(true);
    }
}