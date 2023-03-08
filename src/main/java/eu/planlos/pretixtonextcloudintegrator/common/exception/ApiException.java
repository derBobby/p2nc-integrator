package eu.planlos.pretixtonextcloudintegrator.common.exception;

import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.Serial;

@Getter
public class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 0L;

    private final Integer statusCode;

    public ApiException(WebClientResponseException e) {
        super(e.getMessage());
        this.statusCode = e.getRawStatusCode();
    }

    public enum Cause {
        IS_NULL("Result is null");

        private final String cause;

        Cause(String cause) {
            this.cause = cause;
        }
    }

    public ApiException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiException(Cause cause) {
        super(cause.name());
        this.statusCode = null;
    }
}