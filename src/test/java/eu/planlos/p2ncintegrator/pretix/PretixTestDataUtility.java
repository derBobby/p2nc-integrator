package eu.planlos.p2ncintegrator.pretix;

import eu.planlos.javautilities.ZonedDateTimeUtility;
import eu.planlos.p2ncintegrator.pretix.model.*;
import eu.planlos.p2ncintegrator.pretix.model.dto.PretixQnaFilterUpdateDTO;
import eu.planlos.p2ncintegrator.pretix.model.dto.WebHookDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.planlos.p2ncintegrator.pretix.model.dto.PretixSupportedActions.ORDER_APPROVED;

public abstract class PretixTestDataUtility {

    public final String CORRECT_QUESTION_1 = "Question 1?";
    public final String CORRECT_QUESTION_2 = "Question 2?";
    public final String CORRECT_ANSWER_1 = "Answer 1!";
    public final String CORRECT_ANSWER_2 = "Answer 2!";
    public final String IN_CORRECT_QUESTION_1 = "Incorrect question 1?";
    public final String IN_CORRECT_QUESTION_2 = "Incorrect question 2?";
    public final String IN_CORRECT_ANSWER_1 = "Incorrect answer 1!";
    public final String IN_CORRECT_ANSWER_2 = "Incorrect answer 2!";
    public final String EVENT = "zeltlager23ma";
    public final String CODE_NEW = "NC0DE";
    public final String ORGANIZER = "organizer";
    public final PretixId PRETIX_ID = new PretixId(0L);

    /*
     * To be filtered QnaMaps
     */

    /**
     * Provides map matching filter
     * @return map containing QnA
     */
    protected Map<Question, Answer> correctQnaMap() {
        return Map.of(
                new Question(PRETIX_ID, CORRECT_QUESTION_1),    new Answer(PRETIX_ID, CORRECT_ANSWER_1),
                new Question(PRETIX_ID, CORRECT_QUESTION_2),    new Answer(PRETIX_ID, CORRECT_ANSWER_2));
    }


    /**
     * Provides map with missing question compared to the filter
     * @return map containing QnA
     */
    protected Map<Question, Answer> missingQuestionQnaMap() {
        return Map.of(
                new Question(PRETIX_ID, CORRECT_QUESTION_1),    new Answer(PRETIX_ID, CORRECT_ANSWER_1));
    }

    /**
     * Provides map with additional question compared to the filter
     * @return map containing QnA
     */
    protected Map<Question, Answer> additionalQuestionsQnaMap() {
        Map<Question, Answer> map = correctQnaMap();    // Basic
        Map<Question, Answer> additionalMap = Map.of(   // + Additional
                new Question(PRETIX_ID, "Additional?"),     new Answer(PRETIX_ID, "Additional!"));

        return Stream
                .concat(map.entrySet().stream(), additionalMap.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Provides map missing all questions compared to the filter
     * @return map containing QnA
     */
    protected Map<Question, Answer> allQuestionsMissingQnaMap() {
        return Map.of(
                new Question(PRETIX_ID, IN_CORRECT_QUESTION_1), new Answer(PRETIX_ID, CORRECT_ANSWER_1),
                new Question(PRETIX_ID, IN_CORRECT_QUESTION_2), new Answer(PRETIX_ID, CORRECT_ANSWER_2));
    }

    /**
     * Provides map matching all questions but not all answers compared to filter
     * @return map containing QnA
     */
    protected Map<Question, Answer> notAllAnswersCorrectMap() {
        return Map.of(
                new Question(PRETIX_ID, CORRECT_QUESTION_1),    new Answer(PRETIX_ID, CORRECT_ANSWER_1),
                new Question(PRETIX_ID, CORRECT_QUESTION_2),    new Answer(PRETIX_ID, IN_CORRECT_ANSWER_2));
    }

    /**
     * Provides map matching all questions but no answers
     * @return map containing QnA
     */
    protected Map<Question, Answer> noAnswerCorrectMap() {
        return Map.of(
                new Question(PRETIX_ID, CORRECT_QUESTION_1),    new Answer(PRETIX_ID, IN_CORRECT_ANSWER_1),
                new Question(PRETIX_ID, CORRECT_QUESTION_2),    new Answer(PRETIX_ID, IN_CORRECT_ANSWER_2));
    }

    /*
     * Filters
     */

    protected PretixQnaFilter filterOK() {
        return new PretixQnaFilter(
                ORDER_APPROVED.getAction(),
                EVENT,
                Map.of(
                        CORRECT_QUESTION_1,
                        List.of(CORRECT_ANSWER_1),
                        CORRECT_QUESTION_2,
                        List.of(CORRECT_ANSWER_2)));
    }

    /**
     * Must correspont to filterOK()
     * @return filter update dto
     */
    protected PretixQnaFilterUpdateDTO updateFilterOK(Long id) {
        return new PretixQnaFilterUpdateDTO(
                id,
                ORDER_APPROVED.getAction(),
                "UPDATED_EVENT",
                Map.of(
                        CORRECT_QUESTION_1,
                        List.of(CORRECT_ANSWER_1),
                        CORRECT_QUESTION_2,
                        List.of(CORRECT_ANSWER_2)));
    }

    protected PretixQnaFilter filterWithDuplicateAnswer() {
        return new PretixQnaFilter(
                ORDER_APPROVED.getAction(),
                EVENT,
                Map.of(
                        CORRECT_QUESTION_1,
                        List.of(CORRECT_ANSWER_1, CORRECT_ANSWER_1)));
    }

    protected PretixQnaFilter filterWithInvalidAction() {
        return new PretixQnaFilter(
                "invalid.action",
                EVENT,
                Map.of(
                        CORRECT_QUESTION_1,
                        List.of(CORRECT_ANSWER_1, CORRECT_ANSWER_2)));
    }

    protected List<PretixQnaFilter> filterOKList() {
        return List.of(
                new PretixQnaFilter(
                        ORDER_APPROVED.getAction(),
                        EVENT,
                        Map.of(
                                CORRECT_QUESTION_1, List.of(CORRECT_ANSWER_1, CORRECT_ANSWER_2),
                                CORRECT_QUESTION_2, List.of(CORRECT_ANSWER_1, CORRECT_ANSWER_2))));
    }

    /*
     * Webhooks
     */

    protected WebHookDTO orderApprovedHook() {
        return new WebHookDTO(0L, ORGANIZER, EVENT, CODE_NEW, ORDER_APPROVED.getAction());
    }

    /*
     * Booking
     */

    protected Booking ticketBooking() {
        return new Booking(
                EVENT,
                CODE_NEW,
                "First",
                "Last",
                "first.last@example.com",
                ZonedDateTimeUtility.nowCET().toLocalDateTime(),
                ticketPositionList());
    }

    protected Booking addonBooking() {
        return new Booking(
                EVENT,
                CODE_NEW,
                "First",
                "Last",
                "first.last@example.com",
                ZonedDateTimeUtility.nowCET().toLocalDateTime(),
                addonPositionList());
    }

    protected Booking noPositionsBooking() {
        return new Booking(
                EVENT,
                CODE_NEW,
                "First",
                "Last",
                "first.last@example.com",
                ZonedDateTimeUtility.nowCET().toLocalDateTime(),
                Collections.emptyList());
    }

    private List<Position> ticketPositionList() {
        return List.of(new Position(ticketProduct(), correctQnaMap()));
    }

    private List<Position> addonPositionList() {
        return List.of(new Position(addonProduct(), correctQnaMap()));
    }

    private Product ticketProduct() {
        return new Product(PRETIX_ID, "ticket", ticketProductType());
    }

    private Product addonProduct() {
        return new Product(PRETIX_ID, "probably a hoodie", addonProductType());
    }

    private ProductType ticketProductType() {
        return new ProductType(PRETIX_ID, false, "Some kind of ticket");
    }

    private ProductType addonProductType() {
        return new ProductType(PRETIX_ID, true, "Not a ticket, probably a hoodie");
    }
}