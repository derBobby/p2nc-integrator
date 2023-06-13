package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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
    @ManyToMany
    private Map<Question, Answer> QnA;

    @NotNull
    @ManyToOne
    private ProductType productType;

    public Product(Long pretixId, String name, Map<Question, Answer> QnA, ProductType productType) {
        this.pretixId = pretixId;
        this.name = name;
        this.QnA = QnA;
        this.productType = productType;
    }

    public Product(Long pretixId, Long pretixVariationId, String name, Map<Question, Answer> QnA, ProductType productType) {
        this.pretixId = pretixId;
        this.pretixVariationId = pretixVariationId;
        this.name = name;
        this.QnA = QnA;
        this.productType = productType;
    }

    // No questions for Addons
    public Product(Long pretixId, String name, ProductType productType) {
        this.pretixId = pretixId;
        this.name = name;
        this.QnA = new HashMap<>();
        this.productType = productType;
    }

    public Product(Long pretixId, Long pretixVariationId, String name, ProductType productType) {
        this.pretixId = pretixId;
        this.pretixVariationId = pretixVariationId;
        this.name = name;
        this.QnA = new HashMap<>();
        this.productType = productType;
    }
}
