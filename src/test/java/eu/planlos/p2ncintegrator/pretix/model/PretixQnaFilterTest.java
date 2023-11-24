package eu.planlos.p2ncintegrator.pretix.model;

import eu.planlos.p2ncintegrator.pretix.PretixTestDataUtility;
import eu.planlos.p2ncintegrator.pretix.model.dto.PretixSupportedActions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.planlos.p2ncintegrator.pretix.model.dto.PretixSupportedActions.ORDER_APPROVED;
import static org.junit.jupiter.api.Assertions.*;

class PretixQnaFilterTest extends PretixTestDataUtility {

    @Test
    public void filtersCreatedEqually_areEqual() {
        assertEquals(newQnaFilter(), newQnaFilter());
    }

    @Test
    public void filterForActionAndEvent_isRecognized() {
        PretixQnaFilter pretixQnaFilter = new PretixQnaFilter(ORDER_APPROVED.getAction(), EVENT, new HashMap<>());
        assertTrue(pretixQnaFilter.isForAction(ORDER_APPROVED.getAction()));
        assertTrue(pretixQnaFilter.isForEvent(EVENT));
    }

    @Test
    public void filtersNotCreatedEqually_areNotEqual() {
        assertNotEquals(newQnaFilter(), newDifferentQnaFilter());
    }

    @Test
    public void allQnaMatchFilter_isFiltered() {
        assertTrue(filterOK().filterQnA(correctQnaMap()));
    }

    @Test
    public void noQuestionsMatchFilter_isNotFiltered() {
        assertFalse(filterOK().filterQnA(allQuestionsMissingQnaMap()));
    }

    @Test
    public void noAnswerMatchFilter_isNotFiltered() {
        assertFalse(filterOK().filterQnA(noAnswerCorrectMap()));
    }

    @Test
    public void notAllQuestionsMatchFilter_isNotFiltered() {
        assertFalse(filterOK().filterQnA(missingQuestionQnaMap()));
    }

    @Test
    public void notAllAnswersMatchFilter_isNotFiltered() {
        assertFalse(filterOK().filterQnA(notAllAnswersCorrectMap()));
    }

    /*
     * Data helper
     */

    private PretixQnaFilter newQnaFilter() {
        return new PretixQnaFilter(
                PretixSupportedActions.ORDER_NEED_APPROVAL.getAction(),
                EVENT,
                Map.of("Question?", List.of("Wrong Answer!", "Answer!")));
    }

    private PretixQnaFilter newDifferentQnaFilter() {
        return new PretixQnaFilter(
                PretixSupportedActions.ORDER_NEED_APPROVAL.getAction(),
                EVENT,
                Map.of("Different Question?", List.of("Wrong Answer!", "Answer!")));
    }
}