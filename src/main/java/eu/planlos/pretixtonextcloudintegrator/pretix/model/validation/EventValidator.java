package eu.planlos.pretixtonextcloudintegrator.pretix.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EventValidator implements ConstraintValidator<ValidEvent, String> {

    @Override
    public void initialize(ValidEvent constraintAnnotation) {
    }

    @Override
    public boolean isValid(String event, ConstraintValidatorContext context) {
        if (event == null) {
            return false;
        }
        String patternString = "(?i)^[a-z0-9_-]{1,30}$";
        return Pattern.matches(patternString, event);
    }
}
