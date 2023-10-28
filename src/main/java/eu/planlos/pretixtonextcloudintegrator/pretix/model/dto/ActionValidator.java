package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ActionValidator implements ConstraintValidator<ValidAction, String> {

    @Override
    public void initialize(ValidAction constraintAnnotation) {
    }

    @Override
    public boolean isValid(String actionString, ConstraintValidatorContext context) {
        if (actionString == null) {
            return true;
        }
        return PretixSupportedActions.isSupportedAction(actionString);
    }
}
