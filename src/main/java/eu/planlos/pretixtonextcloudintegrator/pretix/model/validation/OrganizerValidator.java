package eu.planlos.pretixtonextcloudintegrator.pretix.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class OrganizerValidator implements ConstraintValidator<ValidOrganizer, String> {

    @Override
    public void initialize(ValidOrganizer constraintAnnotation) {
    }

    @Override
    public boolean isValid(String organizer, ConstraintValidatorContext context) {
        if (organizer == null) {
            return true;
        }
        String patternString = "(?i)^[a-z0-9_-]{1,30}$";
        return Pattern.matches(patternString, organizer);
    }
}