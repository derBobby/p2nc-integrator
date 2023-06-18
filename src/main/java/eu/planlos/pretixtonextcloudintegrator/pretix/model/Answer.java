package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@ToString
@NoArgsConstructor
public final class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Long questionPretixId;

    @NotNull
    @Lob
    private String text;

    public Answer(@NotNull Long questionPretixId, @NotNull String text) {
        this.questionPretixId = questionPretixId;
        this.text = text;
    }
}
