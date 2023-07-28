package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import eu.planlos.pretixtonextcloudintegrator.pretix.PretixTestDataUtility;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixQnaFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class PretixEventFilterConfigTest extends PretixTestDataUtility {

    @Test
    public void duplicateQnaFilter_throwsException() {
        // Prepare
        //      objects
        List<PretixQnaFilter> duplicatePretixQnaFilterList = List.of(
                new PretixQnaFilter(Map.of(CORRECT_QUESTION_1, List.of("Wrong Answer!", "Answer!"))),
                new PretixQnaFilter(Map.of(CORRECT_QUESTION_1, List.of("Wrong Answer!", "Answer!"))));
        //      methods

        // Act
        // Check
        Assertions.assertThrows(IllegalArgumentException.class, () -> PretixEventFilterConfig.with(Map.of(newEvent(), duplicatePretixQnaFilterList)));
    }
}