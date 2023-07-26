package eu.planlos.pretixtonextcloudintegrator.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QnaFilterTest {

    @Test
    public void filtersCreatedEqually_areEqual() {
        // Prepare
        //      objects
        QnaFilter qnaFilter1 = newQnaFilter();
        QnaFilter qnaFilter2 = newQnaFilter();
        //      methods

        // Act
        print(qnaFilter1);
        print(qnaFilter2);
        boolean equals = qnaFilter1.equals(qnaFilter2);

        // Check
        assertTrue(equals);
    }

    @Test
    public void filtersNotCreatedEqually_areNotEqual() {
        // Prepare
        //      objects
        QnaFilter qnaFilter1 = newQnaFilter();
        QnaFilter qnaFilter2 = newDifferentQnaFilter();
        //      methods

        // Act
        print(qnaFilter1);
        print(qnaFilter2);
        boolean equals = qnaFilter1.equals(qnaFilter2);

        // Check
        assertFalse(equals);
    }

    private QnaFilter newQnaFilter() {
        return new QnaFilter(Map.of("Question?", List.of("Wrong Answer!", "Answer!")));
    }

    private QnaFilter newDifferentQnaFilter() {
        return new QnaFilter(Map.of("Different Question?", List.of("Wrong Answer!", "Answer!")));
    }

    private void print(Object object) {
        System.out.println(object);
    }
}