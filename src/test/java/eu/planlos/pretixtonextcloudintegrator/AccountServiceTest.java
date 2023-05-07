package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.nextcloud.service.NextcloudApiUserService;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.InvoiceAddressDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.NamePartsDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.OrderDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixApiOrderService;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.common.mail.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @Mock
    PretixApiOrderService pretixApiOrderService;

    @Mock
    NextcloudApiUserService nextcloudApiUserService;

    @Mock
    MailService mailService;

    @InjectMocks
    AccountService accountService;

    @Test
    public void useridGeneration_noUsersYet() {

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
        accountService.handle(webHookDTO);

        // Check
        verify(nextcloudApiUserService).createUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void useridGeneration_mailAndUserUnused() {

        // Prepare
        //      existing
        final String existingUserid1 = "existinguserid1";
        final String existingUserMail1 = "existinguser1@exmaple.com";
        //      order
        final NamePartsDTO namePartsDTO1 = new NamePartsDTO("John", "Doe");
        final InvoiceAddressDTO invoiceAddressDTO1 = new InvoiceAddressDTO(namePartsDTO1.given_name() + " " + namePartsDTO1.family_name(), namePartsDTO1);
        final OrderDTO orderDTO1 = new OrderDTO("c0d3X", invoiceAddressDTO1, "newsuser@example.com", null, null);
        final WebHookDTO webHookDTO = new WebHookDTO(1337L, "organizer", "event", orderDTO1.getCode(), "pretix.event.order.approved");
        //      methods
        when(pretixApiOrderService.fetchOrderFromPretix(orderDTO1.getCode())).thenReturn(orderDTO1);
        when(nextcloudApiUserService.getAllUsersAsUseridEmailMap()).thenReturn(Map.of(existingUserid1, existingUserMail1));

        // Act
        accountService.handle(webHookDTO);

        // Check
        verify(nextcloudApiUserService).createUser("kv-kraichgau-jdoe", orderDTO1.getEmail(), orderDTO1.getFirstName(), orderDTO1.getLastName());
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
        accountService.handle(webHookDTO);

        // Check
        verify(nextcloudApiUserService, times(0)).createUser(anyString(), anyString(), anyString(), anyString());

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
        accountService.handle(webHookDTO);

        // Check
        verify(nextcloudApiUserService).createUser(matches("kv-kraichgau-jodoe"), anyString(), anyString(), anyString());
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
        // TODO mock mail sending?

        // Act
        accountService.handle(webHookDTO);

        // Check
        verify(nextcloudApiUserService, times(0)).createUser(anyString(), anyString(), anyString(), anyString());

        //TODO CONTINUE HERE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        verify(mailService).notifyAdmin(matches(AccountService.SUBJECT_FAIL), anyString());
    }
}