package eu.planlos.pretixtonextcloudintegrator.api.pretix.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public String getName() {
        return invoice_address.name();
    }
}