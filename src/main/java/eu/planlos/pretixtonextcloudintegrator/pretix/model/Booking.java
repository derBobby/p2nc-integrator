package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import java.util.Map;

public record Booking(
        Map<Question, Answer> QnA) {
}
