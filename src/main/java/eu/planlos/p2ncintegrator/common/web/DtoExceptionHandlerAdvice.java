package eu.planlos.p2ncintegrator.common.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class DtoExceptionHandlerAdvice {

    /**
     * This Handler can be used if a REST request sends an invalid @RequestBody
     * to the REST method. An invalid request would result in an
     * {@link MethodArgumentNotValidException}
     *
     * @param ex Exception which should be handled
     * @return Map that contains all field errors
     */
    //TODO necessary?
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));
        return errorMap;
    }

    @ExceptionHandler(DtoValidationException.class)
    public ResponseEntity<DtoErrorResponse> handleValidationException(DtoValidationException ex) {
        return new ResponseEntity<>(
                new DtoErrorResponse(ex.getMessage(), ex.getValidationErrors()),
                HttpStatus.BAD_REQUEST);
    }
}