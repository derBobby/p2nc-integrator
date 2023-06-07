package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single;

import org.springframework.lang.NonNull;

public record AnswerDTO(
        @NonNull Long question,
        @NonNull String answer) {
}