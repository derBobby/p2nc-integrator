package eu.planlos.pretixtonextcloudintegrator.api.pretix.webhook.model;

import eu.planlos.pretixtonextcloudintegrator.common.util.ZonedDateTimeUtility;
import lombok.*;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class AuditEntry {

    private Long id;

    private ZonedDateTime date;

    private String message;

    public AuditEntry(String message) {
        this.message = message;
        this.date = ZonedDateTimeUtility.nowCET();
    }

    public String toString() {
        return String.format("%s: %s - %s", ZonedDateTimeUtility.nice(date), id, message);
    }
}