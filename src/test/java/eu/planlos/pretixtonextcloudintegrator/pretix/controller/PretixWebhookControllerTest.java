package eu.planlos.pretixtonextcloudintegrator.pretix.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.planlos.pretixtonextcloudintegrator.common.audit.AuditService;
import eu.planlos.pretixtonextcloudintegrator.pretix.IPretixWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.pretix.PretixTestDataUtility;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Creds to:
 * https://reflectoring.io/spring-boot-web-controller-test/
 */
@WebMvcTest(controllers = PretixWebhookController.class)
class PretixWebhookControllerTest extends PretixTestDataUtility {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditService webHookAuditService;

    @MockBean
    private IPretixWebHookHandler webHookHandler;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void correctHook_returnsOK() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderApprovedHookJson()))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    public void hookUsesInvalidAction_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(wrongActionHookJson()))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    String errorMessage = result.getResolvedException().getMessage();
                    assertTrue(errorMessage.contains("Validation Error"));
                    assertTrue(errorMessage.contains("Invalid action"));
                });
    }

    @Test
    public void organizerInHookIsNull_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(missingOrganizerActionHookJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    /*
     * Helper
     */

    private String orderApprovedHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, ORGANIZER, EVENT, CODE_NEW, ACTION_ORDER_APPROVED));
    }

    private String wrongActionHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, ORGANIZER, EVENT, CODE_NEW, "wrong-action"));
    }

    private String missingOrganizerActionHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, null, EVENT, CODE_NEW, ACTION_ORDER_APPROVED));
    }
}