package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.SupportedActions;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.WebHookDTONotValidException;

import java.util.List;

//TODO create tests
public class WebHookDTOValidator {

    private final List<String> VALID_ACTIONS = List.of(SupportedActions.ORDER_NEED_APPROVAL, SupportedActions.ORDER_APPROVED);

    public void validateOrThrowException(WebHookDTO hook) {
        String STRING_MATCHER = "(?i)^[a-z0-9_-]{1,30}$";
        String CODE_MATCHER = "(?i)^[a-z0-9_-]{5}$";
        if (!hook.organizer().matches(STRING_MATCHER)) throw new WebHookDTONotValidException("Organizer is not valid");
        if (!hook.event().matches(STRING_MATCHER)) throw new WebHookDTONotValidException("Event is not valid");
        if (!hook.code().matches(CODE_MATCHER)) throw new WebHookDTONotValidException("Code is not valid");
        if (!VALID_ACTIONS.contains(hook.action())) throw new WebHookDTONotValidException("Action is not valid");
    }
}