package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.config.EventFilterConfig;
import eu.planlos.pretixtonextcloudintegrator.model.QnaFilter;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Answer;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Question;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QnaFilterServiceTest extends TestDataUtility {

    @Test
    public void matchesAllQnA_isTrue() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMap();
        String event = newEvent();
        EventFilterConfig config = EventFilterConfig.with(Map.of(newEvent(), filterMatchesQuestionAndAnswer()));
        QnaFilterService qnaFilterService = new QnaFilterService(config);
        //      methods

        // Act
        boolean containsMatch = qnaFilterService.filter(event, qnaMap);

        // Check
        assertTrue(containsMatch);
    }

    @Test
    public void matchesAllQnAWithAdditionalQuestionsInFilter_isTrue() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMapAdditionalQuestions();
        String event = newEvent();
        EventFilterConfig config = EventFilterConfig.with(Map.of(newEvent(), filterMatchesQuestionAndAnswer()));
        QnaFilterService qnaFilterService = new QnaFilterService(config);
        //      methods

        // Act
        boolean containsMatch = qnaFilterService.filter(event, qnaMap);

        // Check
        assertTrue(containsMatch);
    }

    @Test
    public void matchesOnlyQuestions_isFalse() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMap();
        String event = newEvent();
        EventFilterConfig config = EventFilterConfig.with(Map.of(newEvent(), filterMatchesOnlyQuestion()));
        QnaFilterService qnaFilterService = new QnaFilterService(config);
        //      methods

        // Act
        boolean containsMatch = qnaFilterService.filter(event, qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    @Test
    public void matchesNotAllQuestions_isFalse() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMap();
        String event = newEvent();
        EventFilterConfig config = EventFilterConfig.with(Map.of(newEvent(), filterMatchesNotAllQuestions()));
        QnaFilterService qnaFilterService = new QnaFilterService(config);
        //      methods

        // Act
        boolean containsMatch = qnaFilterService.filter(event, qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    @Test
    public void matchesNoQuestion_isFalse() {
        // Prepare
        //      objects
        String event = newEvent();
        Map<Question, Answer> qnaMap = newQnaMap();
        EventFilterConfig config = EventFilterConfig.with(Map.of(newEvent(), filterMatchesNoQuestion()));
        QnaFilterService qnaFilterService = new QnaFilterService(config);
        //      methods

        // Act
        boolean containsMatch = qnaFilterService.filter(event, qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    private List<QnaFilter> filterMatchesNoQuestion() {
        return List.of(new QnaFilter(Map.of(
                "Wrong question 1?", List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                "Wrong question 2?", List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!"))));
    }

    private List<QnaFilter> filterMatchesQuestionAndAnswer() {
        return List.of(new QnaFilter(Map.of(
                CORRECT_QUESTION_1, List.of(CORRECT_ANSWER_1, "Wrong Answer!"),
                CORRECT_QUESTION_2, List.of("Wrong Answer!", CORRECT_ANSWER_2))));
    }

    private List<QnaFilter> filterMatchesOnlyQuestion() {
        return List.of(new QnaFilter(Map.of(
                CORRECT_QUESTION_1, List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                CORRECT_QUESTION_2, List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!"))));
    }

    private List<QnaFilter> filterMatchesNotAllQuestions() {
        return List.of(new QnaFilter(Map.of(
                "Wrong question?", List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                CORRECT_QUESTION_2, List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!"))));
    }
}