package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.PretixTestDataUtility;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Answer;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixQnaFilter;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Question;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.PretixSupportedActions.ORDER_APPROVED;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PretixEventFilterServiceIT extends PretixTestDataUtility {

    @Test
    public void matchesAllQnA_isTrue() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newCorrectQnaMap();
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(filterMatchesQuestionAndAnswer());
        //      methods

        // Act
        boolean containsMatch = pretixEventFilterService.filter(ORDER_APPROVED.getAction(), EVENT, qnaMap);

        // Check
        assertTrue(containsMatch);
    }

    @Test
    public void matchesAllQnAWithAdditionalQuestionsInFilter_isTrue() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newAdditionalQuestionsQnaMap();
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(filterMatchesQuestionAndAnswer());
        //      methods

        // Act
        boolean containsMatch = pretixEventFilterService.filter(ORDER_APPROVED.getAction(), EVENT, qnaMap);

        // Check
        assertTrue(containsMatch);
    }

    @Test
    public void matchesOnlyQuestions_isFalse() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newCorrectQnaMap();
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(filterMatchesOnlyQuestion());
        //      methods

        // Act
        boolean containsMatch = pretixEventFilterService.filter(ORDER_APPROVED.getAction(), EVENT, qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    @Test
    public void matchesNotAllQuestions_isFalse() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newCorrectQnaMap();
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(filterMatchesNotAllQuestions());
        //      methods

        // Act
        boolean containsMatch = pretixEventFilterService.filter(ORDER_APPROVED.getAction(), EVENT, qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    @Test
    public void matchesNoQuestion_isFalse() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newCorrectQnaMap();
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(filterMatchesNoQuestion());
        //      methods

        // Act
        boolean containsMatch = pretixEventFilterService.filter(ORDER_APPROVED.getAction(), EVENT, qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    /*
     * Support methods
     */
    private List<PretixQnaFilter> filterMatchesNoQuestion() {
        return List.of(
                new PretixQnaFilter(
                        ORDER_APPROVED.getAction(),
                        EVENT,
                        Map.of(
                                "Wrong question 1?", List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                                "Wrong question 2?", List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!"))));
    }

    private List<PretixQnaFilter> filterMatchesQuestionAndAnswer() {
        return List.of(
                new PretixQnaFilter(
                        ORDER_APPROVED.getAction(),
                        EVENT,
                        Map.of(
                                CORRECT_QUESTION_1, List.of(CORRECT_ANSWER_1, "Wrong Answer!"),
                                CORRECT_QUESTION_2, List.of("Wrong Answer!", CORRECT_ANSWER_2))));
    }

    private List<PretixQnaFilter> filterMatchesOnlyQuestion() {
        return List.of(
                new PretixQnaFilter(
                        ORDER_APPROVED.getAction(),
                        EVENT,
                        Map.of(
                                CORRECT_QUESTION_1, List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                                CORRECT_QUESTION_2, List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!"))));
    }

    private List<PretixQnaFilter> filterMatchesNotAllQuestions() {
        return List.of(
                new PretixQnaFilter(
                        ORDER_APPROVED.getAction(),
                        EVENT,
                        Map.of(
                                "Wrong question?", List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                                CORRECT_QUESTION_2, List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!"))));
    }
}