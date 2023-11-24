package eu.planlos.p2ncintegrator.pretix.service;

import eu.planlos.p2ncintegrator.pretix.PretixTestDataUtility;
import eu.planlos.p2ncintegrator.pretix.repository.PretixQnaFilterRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static eu.planlos.p2ncintegrator.pretix.model.dto.PretixSupportedActions.ORDER_APPROVED;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class PretixEventFilterServiceIT extends PretixTestDataUtility {

    @Autowired
    private PretixQnaFilterRepository pretixQnaFilterRepository;

    private PretixEventFilterService pretixEventFilterService;

    @BeforeEach
    public void setUp() {
        pretixEventFilterService = new PretixEventFilterService(pretixQnaFilterRepository, filterOKList());
    }

    /*
     * Violation tests
     */

    @Test
    public void duplicateAnswer_throwsException() {
        pretixEventFilterService.addFilter(filterWithDuplicateAnswer());
        assertFlushResultsInConstraingViolation();
    }

    @Test
    public void invalidAction_throwsException() {
        pretixEventFilterService.addFilter(filterWithInvalidAction());
        assertFlushResultsInConstraingViolation();
    }

    /*
     * Filter tests
     */

    @Test
    public void goodQnA_matchesFilter() {
        assertTrue(pretixEventFilterService.matchesQnaFilter(ORDER_APPROVED.getAction(), EVENT, correctQnaMap()));
    }

    @Test
    public void missingQuestionQna_doesntMatchFilter() {
        assertFalse(pretixEventFilterService.matchesQnaFilter(ORDER_APPROVED.getAction(), EVENT, missingQuestionQnaMap()));
    }

    @Test
    public void moreQnAThanInFilter_matchesFilter() {
        assertTrue(pretixEventFilterService.matchesQnaFilter(ORDER_APPROVED.getAction(), EVENT, additionalQuestionsQnaMap()));
    }

    @Test
    public void noQuestionAnswered_isFalse() {
        assertFalse(pretixEventFilterService.matchesQnaFilter(ORDER_APPROVED.getAction(), EVENT, allQuestionsMissingQnaMap()));
    }

    @Test
    public void notAllAnswersCorrect_isFalse() {
        assertFalse(pretixEventFilterService.matchesQnaFilter(ORDER_APPROVED.getAction(), EVENT, notAllAnswersCorrectMap()));
    }

    @Test
    public void allAnswersWrong_isFalse() {
        assertFalse(pretixEventFilterService.matchesQnaFilter(ORDER_APPROVED.getAction(), EVENT, noAnswerCorrectMap()));
    }

    /*
     * Assert helpers
     */

    private void assertFlushResultsInConstraingViolation() {
        assertThrows(ConstraintViolationException.class, () -> pretixQnaFilterRepository.flush());
    }
}