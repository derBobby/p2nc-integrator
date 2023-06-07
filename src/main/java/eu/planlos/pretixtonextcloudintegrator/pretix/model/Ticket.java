package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Entity
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public final class Ticket extends Product {
@Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToMany
    private Map<Question, Answer> QnA;
}
