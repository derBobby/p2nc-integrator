package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.Answer;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Question;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class QnaFilterServiceTest extends TestGenerator {

    @Test
    public void filterMap_containsMatch() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = qnaMap();
        QnaFilterConfig config = new QnaFilterConfig(matchConfigList());
        QnaFilterService qnaFilterService = new QnaFilterService(config);
        //      methods


        // Act
        boolean containsMatch = qnaFilterService.filter(qnaMap);

        // Check
        assertTrue(containsMatch);
    }

    @Test
    public void filterMap_containsNoMatch() {
        // Prepare
        //      objects
        Map<Question, Answer> qnaMap = qnaMap();
        QnaFilterConfig config = new QnaFilterConfig(noMatchConfigList());
        QnaFilterService qnaFilterService = new QnaFilterService(config);
        //      methods

        // Act
        boolean containsMatch = qnaFilterService.filter(qnaMap);

        // Check
        assertFalse(containsMatch);
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicateFilter_throwsException() {
        // Prepare
        //      objects
        QnaFilterConfig config = new QnaFilterConfig(duplicateEntryList());
        //      methods

        // Act
        new QnaFilterService(config);

        // Check
    }

    private List<Map<String, List<String>>> noMatchConfigList() {
        return List.of(
                Map.of("Wrong question 1?", List.of("Wrong Answer 1.1!", "Wrong Answer 1.2!")),
                Map.of("Wrong question 2?", List.of("Wrong Answer 2.1!", "Wrong Answer 2.2!")));
    }

    private List<Map<String, List<String>>> matchConfigList() {
        return List.of(
                Map.of("Wrong question?", List.of("Wrong Answer 1!", "Wrong Answer 2!")),
                Map.of("Question?", List.of("Wrong Answer!", "Answer!")));
    }

    private List<Map<String, List<String>>> duplicateEntryList() {
        return List.of(
                Map.of("Question?", List.of("Wrong Answer!", "Answer!")),
                Map.of("Question?", List.of("Wrong Answer!", "Answer!")));
    }
}