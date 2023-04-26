package eu.planlos.pretixtonextcloudintegrator.api.pretix.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
public final class OrderDTO {

    private String code;
    private InvoiceAddressDTO invoice_address;
    private String email;
    private LocalDateTime expires;
    private List<PositionDTO> positions;

    public OrderDTO(
            String code,
            InvoiceAddressDTO invoice_address,
            String email,
            LocalDateTime expires,
            List<PositionDTO> positions
    ) {
        this.code = code;
        this.invoice_address = invoice_address;
        this.email = email;
        this.expires = expires;
        this.positions = positions;
    }

    public String getFirstName() {
        return invoice_address.name_parts().given_name();
    }
    public String getLastName() {
        return invoice_address.name_parts().family_name();
    }

    @Override
    public String toString() {
        return String.format("code=%s, firstname=%s, lastname=%s, email=%s", code, getFirstName(), getLastName(), email);
    }
}