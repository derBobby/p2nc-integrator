package eu.planlos.p2ncintegrator.pretix;

import eu.planlos.p2ncintegrator.pretix.model.Answer;
import eu.planlos.p2ncintegrator.pretix.model.PretixId;
import eu.planlos.p2ncintegrator.pretix.model.PretixQnaFilter;
import eu.planlos.p2ncintegrator.pretix.model.Question;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.planlos.p2ncintegrator.pretix.model.dto.PretixSupportedActions.ORDER_APPROVED;

public abstract class PretixTestDataUtility {

    public final String CORRECT_QUESTION_1 = "Question 1?";
    public final String CORRECT_ANSWER_1 = "Answer 1!";
    public final String CORRECT_QUESTION_2 = "Question 2?";
    public final String CORRECT_ANSWER_2 = "Answer 2!";
    public final String EVENT = "zeltlager23ma";
    public final String CODE_NEW = "NC0DE";
    public final String ORGANIZER = "organizer";
    public final PretixId PRETIX_ID = new PretixId(0L);

    /*
     * To be filtered QnaMaps
     */

    protected Map<Question, Answer> newCorrectQnaMap() {
        return Map.of(
                new Question(PRETIX_ID, CORRECT_QUESTION_1),
                new Answer(PRETIX_ID, CORRECT_ANSWER_1),
                new Question(PRETIX_ID, CORRECT_QUESTION_2),
                new Answer(PRETIX_ID, CORRECT_ANSWER_2));
    }

    protected Map<Question, Answer> newPartiallyCorrectQuestionsQnaMap() {
        return Map.of(
                new Question(PRETIX_ID, CORRECT_QUESTION_1),
                new Answer(PRETIX_ID, CORRECT_ANSWER_1),
                new Question(PRETIX_ID, "!" + CORRECT_QUESTION_2),
                new Answer(PRETIX_ID, CORRECT_ANSWER_2));
    }

    protected Map<Question, Answer> newPartiallyCorrectAnswesQnaMap() {
        return Map.of(
                new Question(PRETIX_ID, CORRECT_QUESTION_1),
                new Answer(PRETIX_ID, CORRECT_ANSWER_1),
                new Question(PRETIX_ID, "!" + CORRECT_QUESTION_2),
                new Answer(PRETIX_ID, CORRECT_ANSWER_2));
    }

    protected Map<Question, Answer> newIncorrectQuestionQnaMap() {
        return Map.of(
                new Question(PRETIX_ID, "!" + CORRECT_QUESTION_1),
                new Answer(PRETIX_ID, CORRECT_ANSWER_1),
                new Question(PRETIX_ID, "!" + CORRECT_QUESTION_2),
                new Answer(PRETIX_ID, CORRECT_ANSWER_2));
    }

    protected Map<Question, Answer> newIncorrectAnswerQnaMap() {
        return Map.of(
                new Question(PRETIX_ID, CORRECT_QUESTION_1),
                new Answer(PRETIX_ID, "!" + CORRECT_ANSWER_1),
                new Question(PRETIX_ID, CORRECT_QUESTION_2),
                new Answer(PRETIX_ID, "!" + CORRECT_ANSWER_2));
    }

    protected Map<Question, Answer> newAdditionalQuestionsQnaMap() {

        // Basic
        Map<Question, Answer> map = newCorrectQnaMap();
        // + Addiotional
        Map<Question, Answer> additionalMap = Map.of(
                new Question(PRETIX_ID, "Additional Question"),
                new Answer(PRETIX_ID, "Additional Answer"));

        return Stream
                .concat(map.entrySet().stream(), additionalMap.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /*
     * Filters
     */

    protected PretixQnaFilter newCorrectQnaFilter() {
        return new PretixQnaFilter(
                ORDER_APPROVED.getAction(),
                EVENT,
                Map.of(
                        CORRECT_QUESTION_1,
                        List.of(CORRECT_ANSWER_1),
                        CORRECT_QUESTION_2,
                        List.of(CORRECT_ANSWER_2)));
    }
}