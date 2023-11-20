package eu.planlos.p2ncintegrator.pretix.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@NoArgsConstructor(force = true)
@Getter
@ToString
@EqualsAndHashCode
public final class PretixId {
    private final Long value;

    public PretixId(@NotNull Long value) {
        this.value = value;
    }
}

