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
public final class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "pretix_id"))
    })
    private PretixId pretixId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "pretix_variation_id"))
    })
    private PretixId pretixVariationId;

    @NotNull
    private String name;

    @NotNull
    @ManyToOne
    private ProductType productType;

    public Product(PretixId pretixId, String name, ProductType productType) {
        this.pretixId = pretixId;
        this.name = name;
        this.productType = productType;
    }

    public Product(PretixId pretixId, PretixId pretixVariationId, String name, ProductType productType) {
        this.pretixId = pretixId;
        this.pretixVariationId = pretixVariationId;
        this.name = name;
        this.productType = productType;
    }
}
