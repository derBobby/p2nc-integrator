package eu.planlos.p2ncintegrator.pretix.service;

import eu.planlos.p2ncintegrator.pretix.PretixTestDataUtility;
import eu.planlos.p2ncintegrator.pretix.model.Answer;
import eu.planlos.p2ncintegrator.pretix.model.PretixQnaFilter;
import eu.planlos.p2ncintegrator.pretix.model.Question;
import eu.planlos.p2ncintegrator.pretix.repository.PretixQnaFilterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static eu.planlos.p2ncintegrator.pretix.model.dto.PretixSupportedActions.ORDER_APPROVED;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class PretixEventFilterServiceIT extends PretixTestDataUtility {

    @Autowired
    private PretixQnaFilterRepository pretixQnaFilterRepository;

    @Test
    public void matchesAllQnA_isTrue() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = newCorrectQnaMap();
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(pretixQnaFilterRepository, filterMatchesQuestionAndAnswer());
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
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(pretixQnaFilterRepository, filterMatchesQuestionAndAnswer());
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
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(pretixQnaFilterRepository, filterMatchesOnlyQuestion());
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
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(pretixQnaFilterRepository, filterMatchesNotAllQuestions());
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
        PretixEventFilterService pretixEventFilterService = new PretixEventFilterService(pretixQnaFilterRepository, filterMatchesNoQuestion());
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