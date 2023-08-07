package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import eu.planlos.pretixtonextcloudintegrator.pretix.PretixTestDataUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class PretixEventFilterTest extends PretixTestDataUtility {

    @Test
    public void differentFilter_notThrowingException() {
        // Prepare
        //      objects
        //      methods

        // Act
        // Check
        new PretixEventFilter(
                newEvent(),
                List.of(
                        new PretixQnaFilter(Map.of(CORRECT_QUESTION_1, List.of("Answer 1", "Answer 2"))),
                        new PretixQnaFilter(Map.of(CORRECT_QUESTION_1, List.of("Answer 1", "Answer 3")))));
    }

    @Test
    public void duplicateQnaFilter_throwsException() {
        // Prepare
        //      objects
        //      methods

        // Act
        // Check
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new PretixEventFilter(
                        newEvent(),
                        List.of(
                                new PretixQnaFilter(Map.of(CORRECT_QUESTION_1, List.of("Answer 1", "Answer 2"))),
                                new PretixQnaFilter(Map.of(CORRECT_QUESTION_1, List.of("Answer 1", "Answer 2"))))));
    }
}