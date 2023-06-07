package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PositionDTO(
        @NotNull String attendee_name,
        @NotNull Long item,
        @NotNull List<AnswerDTO> answers
) {}