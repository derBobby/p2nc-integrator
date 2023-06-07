package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.Booking;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public final class OrderDTO {

    @Id @NotNull
    private String code;
    @NotNull
    private InvoiceAddressDTO invoice_address;
    @NotNull @Email
    private String email;
    @NotNull
    private LocalDateTime expires;
    @NotNull

    private List<PositionDTO> positions;

    public OrderDTO(String code, InvoiceAddressDTO invoice_address, String email, LocalDateTime expires, List<PositionDTO> positions) {
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

    public Booking toOrder() {

        return new Booking(
                this.getCode(),
                this.getFirstName(),
                this.getLastName(),
                this.getEmail(),
                this.getExpires(),
                null,
                null);
    }
}