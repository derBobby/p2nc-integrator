package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.util.ZonedDateTimeUtility;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.*;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;

import java.util.List;
import java.util.Map;

public abstract class TestGenerator {

    protected Booking booking() {
        return new Booking(
                newCode(),
                "First",
                "Last",
                "first.last@example.com",
                ZonedDateTimeUtility.nowCET().toLocalDateTime(),
                positionList());
    }

    protected List<Position> positionList() {
        return List.of(new Position(product(), qnaMap()));
    }

    protected Product product() {
        return new Product(pretixId(), "some product", productTypeTicket());
    }

    protected ProductType productTypeTicket() {
        return new ProductType(pretixId(), false, "some product type");
    }

    protected Map<Question, Answer> qnaMap() {
        return Map.of(new Question(pretixId(), "Question?"), new Answer(pretixId(), "Answer!"));
    }

    protected PretixId pretixId() {
        return new PretixId(0L);
    }

    protected String newCode() {
        return "NC0DE";
    }

    protected WebHookDTO orderApprovedHook() {
        return new WebHookDTO(0L, "organizer", "event", newCode(), "pretix.event.order.approved");
    }
}
