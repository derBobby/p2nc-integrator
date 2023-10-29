package eu.planlos.pretixtonextcloudintegrator.pretix.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class CodeValidator implements ConstraintValidator<ValidCode, String> {

    @Override
    public void initialize(ValidCode constraintAnnotation) {
    }

    @Override
    public boolean isValid(String code, ConstraintValidatorContext context) {
        if (code == null) {
            return false;
        }
        String patternString = "(?i)^[a-z0-9_-]{5}$";
        return Pattern.matches(patternString, code);
    }
}