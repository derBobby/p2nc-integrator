//package eu.planlos.p2ncintegrator.common.web;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@ControllerAdvice
//public class DtoValidationAdvice {
//
//    /**
//     * This handler can be used if a REST request sends an invalid @RequestBody
//     * to the REST method. An invalid request would result in an
//     * {@link MethodArgumentNotValidException}
//     *
//     * @param ex Exception which should be handled
//     * @return Map that contains all field errors
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//
//        ex.getBindingResult().getAllErrors().forEach(error -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//
//        return ResponseEntity.badRequest().body(errors);
//    }
//}
//TODO Where to place?