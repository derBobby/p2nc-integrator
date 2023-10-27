package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto;


import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PretixSupportedActions {
    ORDER_APPROVED("pretix.event.order.approved"),
    ORDER_NEED_APPROVAL("pretix.event.order.placed.require_approval");

    private final String action;

    PretixSupportedActions(String action) {
        this.action = action;
    }

    public static boolean isSupportedAction(String actionString) {
        return Arrays.stream(values())
                .anyMatch(supportedAction -> supportedAction.getAction().equals(actionString));
    }

    public static PretixSupportedActions getEnumByAction(String targetAction) {
        return Arrays.stream(values())
                .filter(action -> action.getAction().equals(targetAction))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Action not available"));
    }
}