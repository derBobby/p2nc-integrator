package eu.planlos.pretixtonextcloudintegrator.pretix.model;

public class WebHookDTONotValidException extends RuntimeException {
    public WebHookDTONotValidException(String message) {
        super(String.format("WebHook is not valid: %s", message));
    }
}
