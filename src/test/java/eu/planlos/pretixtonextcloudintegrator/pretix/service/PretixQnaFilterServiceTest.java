package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.PretixTestDataUtility;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixEventFilterConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixQnaFilter;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Answer;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PretixQnaFilterServiceTest extends PretixTestDataUtility {

    @Mock
    PretixContext pretixContext;

    @Test
    public void matchesAllQnA_isTrue() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMap();
        String event = newEvent();
        PretixEventFilterConfig config = PretixEventFilterConfig.with(Map.of(event, filterMatchesQuestionAndAnswer()));
        PretixQnaFilterService pretixQnaFilterService = new PretixQnaFilterService(config, pretixContext);
        //      methods
        when(pretixContext.getEvent()).thenReturn(event);

        // Act
        boolean containsMatch = pretixQnaFilterService.filter(qnaMap);

        // Check
        assertTrue(containsMatch);
    }

    @Test
    public void matchesAllQnAWithAdditionalQuestionsInFilter_isTrue() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMapAdditionalQuestions();
        String event = newEvent();
        PretixEventFilterConfig config = PretixEventFilterConfig.with(Map.of(event, filterMatchesQuestionAndAnswer()));
        PretixQnaFilterService pretixQnaFilterService = new PretixQnaFilterService(config, pretixContext);
        //      methods
        when(pretixContext.getEvent()).thenReturn(event);

        // Act
        boolean containsMatch = pretixQnaFilterService.filter(qnaMap);

        // Check
        assertTrue(containsMatch);
    }

    @Test
    public void matchesOnlyQuestions_isFalse() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMap();
        String event = newEvent();
        PretixEventFilterConfig config = PretixEventFilterConfig.with(Map.of(newEvent(), filterMatchesOnlyQuestion()));
        PretixQnaFilterService pretixQnaFilterService = new PretixQnaFilterService(config, pretixContext);
        //      methods
        when(pretixContext.getEvent()).thenReturn(event);

        // Act
        boolean containsMatch = pretixQnaFilterService.filter(qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    @Test
    public void matchesNotAllQuestions_isFalse() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newQnaMap();
        String event = newEvent();
        PretixEventFilterConfig config = PretixEventFilterConfig.with(Map.of(newEvent(), filterMatchesNotAllQuestions()));
        PretixQnaFilterService pretixQnaFilterService = new PretixQnaFilterService(config, pretixContext);
        //      methods
        when(pretixContext.getEvent()).thenReturn(event);

        // Act
        boolean containsMatch = pretixQnaFilterService.filter(qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    @Test
    public void matchesNoQuestion_isFalse() {
        // Prepare
        //      objects
        String event = newEvent();
        Map<Question, Answer> qnaMap = newQnaMap();
        PretixEventFilterConfig config = PretixEventFilterConfig.with(Map.of(newEvent(), filterMatchesNoQuestion()));
        PretixQnaFilterService pretixQnaFilterService = new PretixQnaFilterService(config, pretixContext);
        //      methods
        when(pretixContext.getEvent()).thenReturn(event);

        // Act
        boolean containsMatch = pretixQnaFilterService.filter(qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    private List<PretixQnaFilter> filterMatchesNoQuestion() {
        return List.of(new PretixQnaFilter(Map.of(
                "Wrong question 1?", List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                "Wrong question 2?", List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!"))));
    }

    private List<PretixQnaFilter> filterMatchesQuestionAndAnswer() {
        return List.of(new PretixQnaFilter(Map.of(
                CORRECT_QUESTION_1, List.of(CORRECT_ANSWER_1, "Wrong Answer!"),
                CORRECT_QUESTION_2, List.of("Wrong Answer!", CORRECT_ANSWER_2))));
    }

    private List<PretixQnaFilter> filterMatchesOnlyQuestion() {
        return List.of(new PretixQnaFilter(Map.of(
                CORRECT_QUESTION_1, List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                CORRECT_QUESTION_2, List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!"))));
    }

    private List<PretixQnaFilter> filterMatchesNotAllQuestions() {
        return List.of(new PretixQnaFilter(Map.of(
                "Wrong question?", List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!"),
                CORRECT_QUESTION_2, List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!"))));
    }
}