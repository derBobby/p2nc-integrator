package eu.planlos.p2ncintegrator.common;

import lombok.Getter;

import java.io.Serial;

@Getter
public class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 0L;

    public static final String IS_NULL = "ApiResponse object is NULL";

    public ApiException(String message) {
        super(message);
    }
}