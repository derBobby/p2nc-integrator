package eu.planlos.pretixtonextcloudintegrator.common.web;


import java.util.Map;

public record DtoErrorResponse(String message, Map<String, String> errors) {
}