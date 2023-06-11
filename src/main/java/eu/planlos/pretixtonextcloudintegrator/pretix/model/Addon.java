package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@ToString
public final class Addon extends Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    public Addon(@NotNull Long pretixId, @NotNull String name, @NotNull ProductType productType) {
        super(pretixId, productType);
        this.name = name;
    }
}

