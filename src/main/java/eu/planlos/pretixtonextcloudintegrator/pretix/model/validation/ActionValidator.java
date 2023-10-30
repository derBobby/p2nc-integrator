package eu.planlos.pretixtonextcloudintegrator.pretix.model.validation;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.PretixSupportedActions;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ActionValidator implements ConstraintValidator<ValidAction, String> {

    @Override
    public void initialize(ValidAction constraintAnnotation) {
    }

    @Override
    public boolean isValid(String action, ConstraintValidatorContext context) {
        if (action == null) {
            return true;
        }
        return PretixSupportedActions.isSupportedAction(action);
    }
}
