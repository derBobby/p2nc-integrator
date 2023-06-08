package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@ToString
@NoArgsConstructor
public final class Addon extends Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Long pretixID;

    @NotNull
    private String name;

    public Addon(@NotNull Long pretixID, @NotNull String name) {
        this.pretixID = pretixID;
        this.name = name;
    }
}

