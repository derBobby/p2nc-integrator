package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@NoArgsConstructor(force = true)
@Getter
@ToString
public final class PretixId {
    private final Long value;

    public PretixId(@NotNull Long value) {
        this.value = value;
    }
}

