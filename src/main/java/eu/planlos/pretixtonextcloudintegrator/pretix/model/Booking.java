package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public final class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String code;

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @NotNull
    @Email
    private String email;

    @NotNull
    private LocalDateTime expires;

    @NotNull
    @OneToMany
    private List<Ticket> ticketList;

    @NotNull
    @OneToMany
    private List<Addon> addonList;

    public Booking(@NotNull String code, @NotNull String firstname, @NotNull String lastname, @NotNull String email, @NotNull LocalDateTime expires, @NotNull List<Ticket> ticketList, @NotNull List<Addon> addonList) {
        this.code = code;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.expires = expires;
        this.ticketList = ticketList;
        this.addonList = addonList;
    }
}