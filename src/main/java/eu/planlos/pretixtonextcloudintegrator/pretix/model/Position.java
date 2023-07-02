package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToMany(cascade = CascadeType.ALL)
    private Map<Question, Answer> QnA;

    @NotNull
    @ManyToOne
    private Product product;

    public Position(Product product, Map<Question, Answer> qnA) {
        this.product = product;
        QnA = qnA;
    }
}
