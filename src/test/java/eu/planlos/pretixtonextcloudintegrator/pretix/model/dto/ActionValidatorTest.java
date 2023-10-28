package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionValidatorTest {

    private final ActionValidator actionValidator = new ActionValidator();

    @Test
    public void actionIsValid_returnsTrue() {
        assertTrue(actionValidator.isValid(PretixSupportedActions.ORDER_APPROVED.getAction(), null));
    }

    @Test
    public void actionIsInvalid_returnsFalse() {
        assertFalse(actionValidator.isValid("fantasy.action", null));
    }
}