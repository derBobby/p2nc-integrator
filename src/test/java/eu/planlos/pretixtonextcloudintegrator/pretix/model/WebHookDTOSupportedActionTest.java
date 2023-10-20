package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebHookDTOSupportedActionTest {

    @Test
    void getAction() {
        String action = WebHookDTOSupportedAction.ORDER_APPROVED.getAction();
        WebHookDTOSupportedAction retrievedEnum = WebHookDTOSupportedAction.getEnumByAction(action);
        assertEquals(WebHookDTOSupportedAction.ORDER_APPROVED, retrievedEnum);
    }
}