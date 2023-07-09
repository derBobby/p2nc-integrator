package eu.planlos.pretixtonextcloudintegrator.nextcloud.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.InvoiceAddressDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.NamePartsDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.OrderDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NextcloudApiUserServiceTest {

    @Test
    public void useridGeneration_noUsersYet() {

        // Prepare
        //      existing
        //      order
        final NamePartsDTO namePartsDTO1 = new NamePartsDTO("John", "Doe");
        final InvoiceAddressDTO invoiceAddressDTO1 = new InvoiceAddressDTO(namePartsDTO1.given_name() + " " + namePartsDTO1.family_name(), namePartsDTO1);
        final OrderDTO orderDTO1 = new OrderDTO("c0d3X", invoiceAddressDTO1, "newsuser@example.com", null, null);
        final WebHookDTO webHookDTO = new WebHookDTO(1337L, "organizer", "event", orderDTO1.getCode(), "pretix.event.order.placed.require_approval");
        //      methods

        // Act

        // Check
    }

    @Test
    public void xxxxxxx_mailAndUserAvailable() {

    }
}