package eu.planlos.pretixtonextcloudintegrator.pretix;

import eu.planlos.pretixtonextcloudintegrator.common.util.ZonedDateTimeUtility;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.*;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PretixTestDataUtility {

    public final String CORRECT_QUESTION_1 = "Question 1?";
    public final String CORRECT_ANSWER_1 = "Answer 1!";
    public final String CORRECT_QUESTION_2 = "Question 2?";
    public final String CORRECT_ANSWER_2 = "Answer 2!";
    public final String EVENT = "zeltlager23ma";

    public final String WEBHOOK_EVENT_ORDER_APPROVED = "pretix.event.order.approved";

    protected Booking booking() {
        return new Booking(
                EVENT,
                newCode(),
                "First",
                "Last",
                "first.last@example.com",
                ZonedDateTimeUtility.nowCET().toLocalDateTime(),
                positionList());
    }

    protected List<Position> positionList() {
        return List.of(new Position(product(), newQnaMap()));
    }

    protected Product product() {
        return new Product(pretixId(), "some product", productTypeTicket());
    }

    protected ProductType productTypeTicket() {
        return new ProductType(pretixId(), false, "some product type");
    }

    protected Map<Question, Answer> newQnaMap() {
        return Map.of(
                new Question(pretixId(), CORRECT_QUESTION_1),
                new Answer(pretixId(), CORRECT_ANSWER_1),
                new Question(pretixId(), CORRECT_QUESTION_2),
                new Answer(pretixId(), CORRECT_ANSWER_2));
    }

    protected Map<Question, Answer> newQnaMapAdditionalQuestions() {
        Map<Question, Answer> map = newQnaMap();
        Map<Question, Answer> additionalMap = Map.of(
                new Question(pretixId(), "Additional Question"),
                new Answer(pretixId(), "Additional Answer"));

        return Stream
                .concat(map.entrySet().stream(), additionalMap.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    protected PretixId pretixId() {
        return new PretixId(0L);
    }

    protected String newCode() {
        return "NC0DE";
    }

    protected WebHookDTO orderApprovedHook() {
        return new WebHookDTO(0L, "organizer", newEvent(), newCode(), WEBHOOK_EVENT_ORDER_APPROVED);
    }

    protected String newEvent() {
        return "event";
    }
}