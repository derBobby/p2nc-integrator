package eu.planlos.pretixtonextcloudintegrator.common;

import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.Serial;

@Getter
public class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 0L;

    public static final String IS_NULL = "ApiResponse object is NULL";

    // TODO How should this be done?
    public ApiException(WebClientResponseException e) {
        super(e.getMessage());
    }

    public ApiException(String message) {
        super(message);
    }
}