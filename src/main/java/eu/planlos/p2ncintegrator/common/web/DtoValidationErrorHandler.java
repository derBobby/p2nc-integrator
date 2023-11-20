package eu.planlos.p2ncintegrator.common.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

/**
 * Controllers outsource the error handling to this Handler.
 * It generates error messages, that are used by the ControllerAdvice to generate readable messages to the requester
 */
@Slf4j
public class DtoValidationErrorHandler {

    public static void handle(BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("Validation failed for request.");

            Map<String, String> validationErrors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                validationErrors.put(error.getField(), error.getDefaultMessage());
            }

            throw new DtoValidationException(validationErrors);
        }
        log.debug("Validation successful for request");
    }
}