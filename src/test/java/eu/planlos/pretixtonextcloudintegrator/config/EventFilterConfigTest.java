package eu.planlos.pretixtonextcloudintegrator.config;

import eu.planlos.pretixtonextcloudintegrator.TestDataUtility;
import eu.planlos.pretixtonextcloudintegrator.model.QnaFilter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EventFilterConfigTest extends TestDataUtility {

    @Test
    public void duplicateQnaFilter_throwsException() {
        // Prepare
        //      objects
        List<QnaFilter> duplicateQnaFilterList = List.of(
                new QnaFilter(Map.of(CORRECT_QUESTION_1, List.of("Wrong Answer!", "Answer!"))),
                new QnaFilter(Map.of(CORRECT_QUESTION_1, List.of("Wrong Answer!", "Answer!"))));
        //      methods

        // Act
        // Check
        assertThrows(IllegalArgumentException.class, () -> EventFilterConfig.with(Map.of(newEvent(), duplicateQnaFilterList)));
    }
}