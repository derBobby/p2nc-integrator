package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
public final class ProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Embedded
    private PretixId pretixId;

    @NotNull
    private boolean addon;

    @NotNull
    private String name;

    public ProductType(@NotNull PretixId pretixId, @NotNull Boolean addon, @NotNull String name) {
        this.pretixId = pretixId;
        this.addon = addon;
        this.name = name;
    }
}