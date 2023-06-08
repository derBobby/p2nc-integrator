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
@NoArgsConstructor
public final class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Long pretixId;

    @NotNull
    private String question;

    public Question(@NotNull Long pretixId, @NotNull String question) {
        this.pretixId = pretixId;
        this.question = question;
    }
}
