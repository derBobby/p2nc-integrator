package eu.planlos.pretixtonextcloudintegrator.pretix.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodeValidator.class)
public @interface ValidCode {
    String message() default "Invalid code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}