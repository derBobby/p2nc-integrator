package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Entity
@Getter
@ToString
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

    public Position(@NotNull Product product, @NotNull Map<Question, Answer> qnA) {
        this.product = product;
        QnA = qnA;
    }
}