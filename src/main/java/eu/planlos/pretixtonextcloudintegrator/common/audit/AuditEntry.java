package eu.planlos.pretixtonextcloudintegrator.common.audit;

import eu.planlos.pretixtonextcloudintegrator.common.util.ZonedDateTimeUtility;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class AuditEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private ZonedDateTime date;

    @NotEmpty
    private String message;

    public AuditEntry(String message) {
        this.message = message;
        this.date = ZonedDateTimeUtility.nowCET();
    }
}