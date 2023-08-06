package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor
public final class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String event;

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
    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Position> positionList;

    public void setPositionList(@NotNull List<Position> positionList) {
        this.positionList = positionList;
    }

    public Booking(@NotNull String event, @NotNull String code, @NotNull String firstname, @NotNull String lastname, @NotNull String email, @NotNull LocalDateTime expires, @NotNull List<Position> positionList) {
        this.code = code;
        this.event = event;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.expires = expires;
        this.positionList = positionList;
    }
}