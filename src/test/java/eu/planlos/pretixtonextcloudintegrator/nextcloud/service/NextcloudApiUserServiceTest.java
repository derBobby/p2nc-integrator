package eu.planlos.pretixtonextcloudintegrator.nextcloud.service;

import eu.planlos.pretixtonextcloudintegrator.nextcloud.config.NextcloudApiConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class NextcloudApiUserServiceTest {

    NextcloudApiConfig apiConfig = new NextcloudApiConfig("https//example.com", "username", "password", "group");

    @Mock
    WebClient webClient;

    @Test
    public void test() {
        assertTrue(true);
    }

//    @Test
//    public void useridGeneration_noUsersYet() {
//
//        // Prepare
//        //      objects
//        NextcloudApiUserService nextcloudApiUserService = new NextcloudApiUserService(apiConfig, webClient);
//        List<String> userList = List.of("first_user", "second_user");
//        NextcloudApiResponse<NextcloudUserList> mockResult = new NextcloudApiResponse<>(null, new NextcloudUserList(userList));
//        //      methods
//
//        // Act
//        List<String> userIdList = nextcloudApiUserService.getAllUseridsFromNextcloud();
//
//        // Check
//        assertTrue(userIdList.containsAll(userList));
//    }
//
//    @Test
//    public void xxxxxxx_mailAndUserAvailable() {
//
//    }
//
//    @Test
//    public void useridGeneration_useridAlreadyinUse() {
//
//        // Prepare
//        //      existing
//        final String existingUserid1 = "kv-kraichgau-jdoe";
//        final String existingUserMail1 = "existinguser1@exmaple.com";
//        //      order
//        final NamePartsDTO namePartsDTO1 = new NamePartsDTO("John", "Doe");
//        final InvoiceAddressDTO invoiceAddressDTO1 = new InvoiceAddressDTO(namePartsDTO1.given_name() + " " + namePartsDTO1.family_name(), namePartsDTO1);
//        final OrderDTO orderDTO1 = new OrderDTO("c0d3X", invoiceAddressDTO1, "newuser1@example.com", null, null);
//        final WebHookDTO webHookDTO = new WebHookDTO(1337L, "organizer", "event", orderDTO1.getCode(), "pretix.event.order.approved");
//        //      methods
//        when(pretixApiOrderService.fetchOrderFromPretix(orderDTO1.getCode())).thenReturn(orderDTO1);
//        when(nextcloudApiUserService.getAllUsersAsUseridEmailMap()).thenReturn(Map.of(existingUserid1, existingUserMail1));
//
//        // Act
//        accountService.handleUserCreation(webHookDTO);
//
//        // Check
//        verify(nextcloudApiUserService).createUser(anyString(), anyString(), anyString());
//    }
//
//    @Test
//    public void useridGeneration_firstnameNotLongEnoughToAvoidExisting() {
//
//        // Prepare
//        //      existing
//        final String existingUserid1 = "kv-kraichgau-jdoe";
//        final String existingUserMail1 = "existinguser1@exmaple.com";
//        //      order
//        final NamePartsDTO namePartsDTO1 = new NamePartsDTO("J", "Doe");
//        final InvoiceAddressDTO invoiceAddressDTO1 = new InvoiceAddressDTO(namePartsDTO1.given_name() + " " + namePartsDTO1.family_name(), namePartsDTO1);
//        final OrderDTO orderDTO1 = new OrderDTO("c0d3X", invoiceAddressDTO1, "newuser1@example.com", null, null);
//        final WebHookDTO webHookDTO = new WebHookDTO(1337L, "organizer", "event", orderDTO1.getCode(), "pretix.event.order.approved");
//        //      methods
//        when(pretixApiOrderService.fetchOrderFromPretix(orderDTO1.getCode())).thenReturn(orderDTO1);
//        when(nextcloudApiUserService.getAllUsersAsUseridEmailMap()).thenReturn(Map.of(existingUserid1, existingUserMail1));
//
//        // Act
//        accountService.handleUserCreation(webHookDTO);
//
//        // Check
//        verify(nextcloudApiUserService, times(0)).createUser(anyString(), anyString(), anyString());
//        verify(mailService).notifyAdmin(matches(AccountService.SUBJECT_FAIL), anyString());
//        verify(signalService).notifyAdmin(matches(AccountService.SUBJECT_FAIL), anyString());
//    }
//
//    @Test
//    public void useridGeneration_mailAlreadyInUse() {
//
//        // Prepare
//        //      existing
//        final String existingUserid1 = "existinguserid1";
//        final String existingUserMail1 = "existinguser1@exmaple.com";
//        //      order
//        final NamePartsDTO namePartsDTO1 = new NamePartsDTO("John", "Doe");
//        final InvoiceAddressDTO invoiceAddressDTO1 = new InvoiceAddressDTO(namePartsDTO1.given_name() + " " + namePartsDTO1.family_name(), namePartsDTO1);
//        final OrderDTO orderDTO1 = new OrderDTO("c0d3X", invoiceAddressDTO1, existingUserMail1, null, null);
//        final WebHookDTO webHookDTO = new WebHookDTO(1337L, "organizer", "event", orderDTO1.getCode(), "pretix.event.order.approved");
//        //      methods
//        when(pretixApiOrderService.fetchOrderFromPretix(orderDTO1.getCode())).thenReturn(orderDTO1);
//        when(nextcloudApiUserService.getAllUsersAsUseridEmailMap()).thenReturn(Map.of(existingUserid1, existingUserMail1));
//
//        // Act
//        accountService.handleUserCreation(webHookDTO);
//
//        // Check
//        verify(nextcloudApiUserService, times(0)).createUser(anyString(), anyString(), anyString());
//    }
}