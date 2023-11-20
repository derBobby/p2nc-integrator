package eu.planlos.p2ncintegrator.pretix.model.dto.single;

import org.springframework.lang.NonNull;

public record AnswerDTO(
        @NonNull Long question,
        @NonNull String answer) {
}