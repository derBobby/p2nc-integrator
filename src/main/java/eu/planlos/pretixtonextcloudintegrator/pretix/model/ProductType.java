package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Long pretixId;

    @NotNull
    private Boolean addon;

    @NotNull
    private String name;

    public ProductType(@NotNull Long pretixId, @NotNull Boolean addon, @NotNull String name) {
        this.pretixId = pretixId;
        this.addon = addon;
        this.name = name;
    }
}