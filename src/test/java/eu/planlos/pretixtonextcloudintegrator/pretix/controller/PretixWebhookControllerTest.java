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

import static eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.PretixSupportedActions.ORDER_APPROVED;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void correctHook_returns200() throws Exception {

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
    public void organizerIsNull_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(missingOrganizerActionHookJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    String errorMessage = result.getResolvedException().getMessage();
                    assertTrue(errorMessage.contains("Validation Error"));
                    assertTrue(errorMessage.contains("Invalid organizer"));
                });
    }

    @Test
    public void organizerContainsSpecialChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(specialCharInOrganizerHookJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    String errorMessage = result.getResolvedException().getMessage();
                    assertTrue(errorMessage.contains("Validation Error"));
                    assertTrue(errorMessage.contains("Invalid organizer"));
                });
    }

    @Test
    public void organizerExceedsChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tooManyCharsInOrganizerHookJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    String errorMessage = result.getResolvedException().getMessage();
                    assertTrue(errorMessage.contains("Validation Error"));
                    assertTrue(errorMessage.contains("Invalid organizer"));
                });

    }

    @Test
    public void eventContainsSpecialChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(specialCharInEventHookJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    String errorMessage = result.getResolvedException().getMessage();
                    assertTrue(errorMessage.contains("Validation Error"));
                    assertTrue(errorMessage.contains("Invalid event"));
                });
    }

    @Test
    public void eventExceedsChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tooManyCharsInEventHookJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    String errorMessage = result.getResolvedException().getMessage();
                    assertTrue(errorMessage.contains("Validation Error"));
                    assertTrue(errorMessage.contains("Invalid event"));
                });
    }

    @Test
    public void codeContainsSpecialChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(specialCharInCodeHookJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    String errorMessage = result.getResolvedException().getMessage();
                    assertTrue(errorMessage.contains("Validation Error"));
                    assertTrue(errorMessage.contains("Invalid code"));
                });

    }

    @Test
    public void codeExceedsChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tooManyCharsInCodeHookJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    String errorMessage = result.getResolvedException().getMessage();
                    assertTrue(errorMessage.contains("Validation Error"));
                    assertTrue(errorMessage.contains("Invalid code"));
                });
    }

    /*
     * Helper
     */

    private String orderApprovedHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, ORGANIZER, EVENT, CODE_NEW, ORDER_APPROVED.getAction()));
    }

    private String wrongActionHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, ORGANIZER, EVENT, CODE_NEW, "wrong-action"));
    }

    private String missingOrganizerActionHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, null, EVENT, CODE_NEW, ORDER_APPROVED.getAction()));
    }

    private String specialCharInOrganizerHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, "<script>alert(\"X\")</script>", EVENT, CODE_NEW, ORDER_APPROVED.getAction()));
    }

    private String tooManyCharsInOrganizerHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, "0123456789012345678901234567891", EVENT, CODE_NEW, ORDER_APPROVED.getAction()));
    }

    private String specialCharInEventHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, ORGANIZER, "<script>alert(\"X\")</script>", CODE_NEW, ORDER_APPROVED.getAction()));
    }

    private String tooManyCharsInEventHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, ORGANIZER, "0123456789012345678901234567891", CODE_NEW, ORDER_APPROVED.getAction()));
    }

    private String specialCharInCodeHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, ORGANIZER, EVENT, "{<?>}", ORDER_APPROVED.getAction()));
    }

    private String tooManyCharsInCodeHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, ORGANIZER, EVENT, "123456", ORDER_APPROVED.getAction()));
    }
}