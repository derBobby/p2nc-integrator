package eu.planlos.pretixtonextcloudintegrator.pretix.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

public class WebHookValidationErrorHandler {

    public static void handle(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

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
    }
}