package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import java.util.List;

public record PositionDTO(
        String attendee_name,
        Long item,
        List<AnswerDTO> answers
) {
}
