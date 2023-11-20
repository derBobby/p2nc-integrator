package eu.planlos.p2ncintegrator.pretix.model;

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

    public Product(@NotNull PretixId pretixId, @NotNull String name, @NotNull ProductType productType) {
        this.pretixId = pretixId;
        this.name = name;
        this.productType = productType;
    }

    public Product(@NotNull PretixId pretixId, PretixId pretixVariationId, @NotNull String name, @NotNull ProductType productType) {
        this.pretixId = pretixId;
        this.pretixVariationId = pretixVariationId;
        this.name = name;
        this.productType = productType;
    }
}
