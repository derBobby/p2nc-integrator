package eu.planlos.pretixtonextcloudintegrator.common.web;

import lombok.Getter;

import java.util.Map;

/**
 * ValidationException.class is used by the ControllerValidationErrorHandler to pass information to a ControllerAdvice
 */
@Getter
public class DtoValidationException extends RuntimeException {

    private final Map<String, String> validationErrors;

    public DtoValidationException(Map<String, String> validationErrors) {
        super("Validation Error");
        this.validationErrors = validationErrors;
    }
}
