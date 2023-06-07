package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public final class Question {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Long pretidId;

    @NotNull
    private String question;

    public Question(@NotNull Long pretidId, @NotNull String question) {
        this.pretidId = pretidId;
        this.question = question;
    }
}
