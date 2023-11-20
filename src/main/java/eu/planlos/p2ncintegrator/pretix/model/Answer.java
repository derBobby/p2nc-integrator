package eu.planlos.p2ncintegrator.pretix.model;

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
    @Embedded
    private PretixId pretixId;

    @NotNull
    @Lob
    private String text;

    public Answer(@NotNull PretixId pretixId, @NotNull String text) {
        this.pretixId = pretixId;
        this.text = text;
    }
}
