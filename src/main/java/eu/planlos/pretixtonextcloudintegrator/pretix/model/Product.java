package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public final class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Long pretixId;

    private Long pretixVariationId;

    @NotNull
    private String name;

    @NotNull
    @ManyToOne
    private ProductType productType;

    public Product(Long pretixId, String name, ProductType productType) {
        this.pretixId = pretixId;
        this.name = name;
        this.productType = productType;
    }

    public Product(Long pretixId, Long pretixVariationId, String name, ProductType productType) {
        this.pretixId = pretixId;
        this.pretixVariationId = pretixVariationId;
        this.name = name;
        this.productType = productType;
    }
}
