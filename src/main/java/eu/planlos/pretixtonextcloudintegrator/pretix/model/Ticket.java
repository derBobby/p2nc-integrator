package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Entity
@Getter
@ToString
public final class Ticket extends Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToMany
    private Map<Question, Answer> QnA;

    public Ticket(@NotNull Long pretixId, Map<Question, Answer> qnA, ProductType productType) {
        super(pretixId, productType);
        QnA = qnA;
    }
}
