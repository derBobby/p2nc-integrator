package eu.planlos.pretixtonextcloudintegrator.pretix.model;


import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SupportedActions {
    ORDER_APPROVED("pretix.event.order.approved"),
    ORDER_NEED_APPROVAL("pretix.event.order.placed.require_approval");

    private final String action;

    SupportedActions(String action) {
        this.action = action;
    }

    public static boolean isSupportedAction(String actionString) {
        return Arrays.stream(values())
                .anyMatch(supportedAction -> supportedAction.getAction().equals(actionString));
    }
}