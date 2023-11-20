package eu.planlos.p2ncintegrator.pretix.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.planlos.p2ncintegrator.common.audit.AuditService;
import eu.planlos.p2ncintegrator.pretix.IPretixWebHookHandler;
import eu.planlos.p2ncintegrator.pretix.PretixTestDataUtility;
import eu.planlos.p2ncintegrator.pretix.model.dto.WebHookDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static eu.planlos.p2ncintegrator.pretix.model.dto.PretixSupportedActions.ORDER_APPROVED;
import static eu.planlos.p2ncintegrator.pretix.model.dto.PretixSupportedActions.ORDER_NEED_APPROVAL;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Creds to:
 * <a href="https://reflectoring.io/spring-boot-web-controller-test/">...</a>
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

    /*
     * Order requires approval
     */

    @Test
    public void orderNeedsApproval_returns200() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(PretixWebhookController.URL_WEBHOOK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(orderNeedsApprovalHookJson()))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    /*
     * Helper
     */

    private String orderNeedsApprovalHookJson() throws JsonProcessingException {
        return mapper.writeValueAsString(
                new WebHookDTO(0L, ORGANIZER, EVENT, CODE_NEW, ORDER_NEED_APPROVAL.getAction()));
    }


    /*
     * Order approved tests
     */

    @Test
    public void correctHook_returns200() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.post(PretixWebhookController.URL_WEBHOOK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderApprovedHookJson()))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void hookUsesInvalidAction_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(PretixWebhookController.URL_WEBHOOK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(wrongActionHookJson()))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Validation Error")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Invalid action")));

    }

    @Test
    public void organizerIsNull_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(PretixWebhookController.URL_WEBHOOK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(missingOrganizerActionHookJson()))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Validation Error")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("must not be null")));
    }

    @Test
    public void organizerContainsSpecialChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(PretixWebhookController.URL_WEBHOOK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(specialCharInOrganizerHookJson()))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Validation Error")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Invalid organizer")));
    }

    @Test
    public void organizerExceedsChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(PretixWebhookController.URL_WEBHOOK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tooManyCharsInOrganizerHookJson()))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Validation Error")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Invalid organizer")));
    }

    @Test
    public void eventContainsSpecialChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(PretixWebhookController.URL_WEBHOOK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(specialCharInEventHookJson()))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Validation Error")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Invalid event")));
    }

    @Test
    public void eventExceedsChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(PretixWebhookController.URL_WEBHOOK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tooManyCharsInEventHookJson()))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Validation Error")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Invalid event")));
    }

    @Test
    public void codeContainsSpecialChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(PretixWebhookController.URL_WEBHOOK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(specialCharInCodeHookJson()))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Validation Error")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Invalid code")));
    }

    @Test
    public void codeExceedsChars_returns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(PretixWebhookController.URL_WEBHOOK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tooManyCharsInCodeHookJson()))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Validation Error")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Invalid code")));
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