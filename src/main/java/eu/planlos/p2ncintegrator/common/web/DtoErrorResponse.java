package eu.planlos.p2ncintegrator.common.web;


import java.util.Map;

public record DtoErrorResponse(String message, Map<String, String> errors) {
}