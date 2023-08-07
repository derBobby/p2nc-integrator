package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import eu.planlos.pretixtonextcloudintegrator.pretix.PretixTestDataUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PretixQnaFilterTest extends PretixTestDataUtility {
    @Test
    public void duplicateAnswers_throwsException() {
        // Prepare
        //      objects
        //      methods

        // Act
        new PretixQnaFilter(Map.of("Question?", List.of("Answer 1!", "Answer 2!")));
        // Check
        Assertions.assertThrows(IllegalArgumentException.class, () -> new PretixQnaFilter(Map.of("Question?", List.of("Answer 1!", "Answer 1!"))));
    }

    @Test
    public void filtersCreatedEqually_areEqual() {
        // Prepare
        //      objects
        PretixQnaFilter pretixQnaFilter1 = newQnaFilter();
        PretixQnaFilter pretixQnaFilter2 = newQnaFilter();
        //      methods

        // Act
        print(pretixQnaFilter1);
        print(pretixQnaFilter2);
        boolean equals = pretixQnaFilter1.equals(pretixQnaFilter2);

        // Check
        assertTrue(equals);
    }

    @Test
    public void filtersNotCreatedEqually_areNotEqual() {
        // Prepare
        //      objects
        PretixQnaFilter pretixQnaFilter1 = newQnaFilter();
        PretixQnaFilter pretixQnaFilter2 = newDifferentQnaFilter();
        //      methods

        // Act
        print(pretixQnaFilter1);
        print(pretixQnaFilter2);
        boolean equals = pretixQnaFilter1.equals(pretixQnaFilter2);

        // Check
        assertFalse(equals);
    }

    private PretixQnaFilter newQnaFilter() {
        return new PretixQnaFilter(Map.of("Question?", List.of("Wrong Answer!", "Answer!")));
    }

    private PretixQnaFilter newDifferentQnaFilter() {
        return new PretixQnaFilter(Map.of("Different Question?", List.of("Wrong Answer!", "Answer!")));
    }

    private void print(Object object) {
        System.out.println(object);
    }
}