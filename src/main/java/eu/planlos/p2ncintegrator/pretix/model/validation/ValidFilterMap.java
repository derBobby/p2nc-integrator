package eu.planlos.p2ncintegrator.pretix.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FilterMapValidator.class)
public @interface ValidFilterMap {
    String message() default "Invalid filter map";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}