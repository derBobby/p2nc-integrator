package eu.planlos.pretixtonextcloudintegrator.pretix.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WebHookValidationErrorHandler {

    public static void handle(BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("Validation failed for request.");

            Map<String, String> validationErrors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                validationErrors.put(error.getField(), error.getDefaultMessage());
            }

            // Construct and return an error response as a Map
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Validation Error");
            errorResponse.put("errors", validationErrors);

            // Return the error response with a 400 Bad Request status
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorResponse.toString());
        }
        log.debug("Validation successfull for request");
    }
}