package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public abstract class Product {

    @NotNull
    final private Long pretixId;

    @NotNull
    final private ProductType productType;

    protected Product(Long pretixId, ProductType productType) {
        this.pretixId = pretixId;
        this.productType = productType;
    }
}
