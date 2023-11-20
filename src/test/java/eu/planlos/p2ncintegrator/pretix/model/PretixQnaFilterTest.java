package eu.planlos.p2ncintegrator.pretix.model;

import eu.planlos.p2ncintegrator.pretix.PretixTestDataUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.planlos.p2ncintegrator.pretix.model.dto.PretixSupportedActions.ORDER_APPROVED;
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
    public void filterForActionAndEvent_isRecognized() {
        // Prepare
        //      objects
        PretixQnaFilter pretixQnaFilter = new PretixQnaFilter(ORDER_APPROVED.getAction(), EVENT, new HashMap<>());
        //      methods

        // Act
        // Check
        assertTrue(pretixQnaFilter.isForAction(ORDER_APPROVED.getAction()));
        assertTrue(pretixQnaFilter.isForEvent(EVENT));
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

    @Test
    public void allQnaMatchFilter_isFiltered() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newCorrectQnaMap();
        PretixQnaFilter pretixQnaFilter = newCorrectQnaFilter();
        //      methods

        // Act
        boolean found = pretixQnaFilter.filterQnA(qnaMap);

        // Check
        assertTrue(found);
    }

    @Test
    public void noQuestionsMatchFilter_isNotFiltered() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newIncorrectQuestionQnaMap();
        PretixQnaFilter pretixQnaFilter = newCorrectQnaFilter();
        //      methods

        // Act
        boolean found = pretixQnaFilter.filterQnA(qnaMap);

        // Check
        assertFalse(found);
    }

    @Test
    public void noAnswerMatchFilter_isNotFiltered() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newIncorrectAnswerQnaMap();
        PretixQnaFilter pretixQnaFilter = newCorrectQnaFilter();
        //      methods

        // Act
        boolean found = pretixQnaFilter.filterQnA(qnaMap);

        // Check
        assertFalse(found);
    }

    @Test
    public void notAllQuestionsMatchFilter_isNotFiltered() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newPartiallyCorrectQuestionsQnaMap();
        PretixQnaFilter pretixQnaFilter = newCorrectQnaFilter();
        //      methods

        // Act
        boolean found = pretixQnaFilter.filterQnA(qnaMap);

        // Check
        assertFalse(found);
    }

    @Test
    public void notAllAnswersMatchFilter_isNotFiltered() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newPartiallyCorrectAnswesQnaMap();
        PretixQnaFilter pretixQnaFilter = newCorrectQnaFilter();
        //      methods

        // Act
        boolean found = pretixQnaFilter.filterQnA(qnaMap);

        // Check
        assertFalse(found);
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