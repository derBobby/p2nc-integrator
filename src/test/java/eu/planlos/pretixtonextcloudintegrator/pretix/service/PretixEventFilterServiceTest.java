package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.PretixTestDataUtility;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixEventFilterConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Answer;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixEventFilter;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixQnaFilter;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PretixEventFilterServiceTest extends PretixTestDataUtility {

    @Test
    public void matchesAllQnA_isTrue() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMap();
        String event = newEvent();
        PretixEventFilterConfig config = PretixEventFilterConfig.with(filterMatchesQuestionAndAnswer());
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(config);
        //      methods

        // Act
        boolean containsMatch = pretixEventFilterService.filter(event, qnaMap);

        // Check
        assertTrue(containsMatch);
    }

    @Test
    public void matchesAllQnAWithAdditionalQuestionsInFilter_isTrue() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMapAdditionalQuestions();
        String event = newEvent();
        PretixEventFilterConfig config = PretixEventFilterConfig.with(filterMatchesQuestionAndAnswer());
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(config);
        //      methods

        // Act
        boolean containsMatch = pretixEventFilterService.filter(event, qnaMap);

        // Check
        assertTrue(containsMatch);
    }

    @Test
    public void matchesOnlyQuestions_isFalse() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMap();
        String event = newEvent();
        PretixEventFilterConfig config = PretixEventFilterConfig.with(filterMatchesOnlyQuestion());
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(config);
        //      methods

        // Act
        boolean containsMatch = pretixEventFilterService.filter(event, qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    @Test
    public void matchesNotAllQuestions_isFalse() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMap();
        String event = newEvent();
        PretixEventFilterConfig config = PretixEventFilterConfig.with(filterMatchesNotAllQuestions());
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(config);
        //      methods

        // Act
        boolean containsMatch = pretixEventFilterService.filter(event, qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    @Test
    public void matchesNoQuestion_isFalse() {
        // Prepare
        //      objects
        String event = newEvent();
        Map<Question, Answer> qnaMap = newQnaMap();
        PretixEventFilterConfig config = PretixEventFilterConfig.with(filterMatchesNoQuestion());
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(config);
        //      methods

        // Act
        boolean containsMatch = pretixEventFilterService.filter(event, qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    private PretixEventFilter filterMatchesNoQuestion() {
        return new PretixEventFilter(newEvent(),
                List.of(new PretixQnaFilter(Map.of(
                        "Wrong question 1?", List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                        "Wrong question 2?", List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!")))));
    }

    private PretixEventFilter filterMatchesQuestionAndAnswer() {
        return new PretixEventFilter(newEvent(),
                List.of(new PretixQnaFilter(Map.of(
                CORRECT_QUESTION_1, List.of(CORRECT_ANSWER_1, "Wrong Answer!"),
                CORRECT_QUESTION_2, List.of("Wrong Answer!", CORRECT_ANSWER_2)))));
    }

    private PretixEventFilter filterMatchesOnlyQuestion() {
        return new PretixEventFilter(newEvent(),
                List.of(new PretixQnaFilter(Map.of(
                CORRECT_QUESTION_1, List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                CORRECT_QUESTION_2, List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!")))));
    }

    private PretixEventFilter filterMatchesNotAllQuestions() {
        return new PretixEventFilter(newEvent(),
                List.of(new PretixQnaFilter(Map.of(
                "Wrong question?", List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                CORRECT_QUESTION_2, List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!")))));
    }
}