package eu.planlos.pretixtonextcloudintegrator.pretix.model.dto;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.WebHookDTOSupportedAction;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.WebHookDTONotValidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class WebHookDTOValidatorTest {

    @Test
    public void allMembersOK_noExceptionThrown() {
        // Prepare
        //      objects
        WebHookDTO webHookDTO = new WebHookDTO(0L, "some-organizer", "some-event", "XC0DE", WebHookDTOSupportedAction.ORDER_APPROVED.getAction());
        WebHookDTOValidator webHookDTOValidator = new WebHookDTOValidator();
        //      methods

        // Act
        // Check
        webHookDTOValidator.validateOrThrowException(webHookDTO);
    }

    @Test
    public void organizerContainsSpecialChars_throwsException() {
        // Prepare
        //      objects
        WebHookDTO webHookDTO = new WebHookDTO(0L, "<script>alert(\"X\")</script>", "some-event", "XC0DE", WebHookDTOSupportedAction.ORDER_APPROVED.getAction());
        //      methods

        // Act
        // Check
        System.out.println("String length: " + webHookDTO.organizer().length());
        assertThrowsException(webHookDTO);
    }

    @Test
    public void organizerExceedsChars_throwsException() {
        // Prepare
        //      objects
        WebHookDTO webHookDTO = new WebHookDTO(0L, "0123456789012345678901234567891", "some-event", "XC0DE", WebHookDTOSupportedAction.ORDER_APPROVED.getAction());
        //      methods

        // Act
        // Check
        System.out.println("String length: " + webHookDTO.organizer().length());
        assertThrowsException(webHookDTO);
    }

    @Test
    public void eventContainsSpecialChars_throwsException() {
        // Prepare
        //      objects
        WebHookDTO webHookDTO = new WebHookDTO(0L, "organizer", "<script>alert(\"X\")</script>", "XC0DE", WebHookDTOSupportedAction.ORDER_APPROVED.getAction());
        //      methods

        // Act
        // Check
        System.out.println("String length: " + webHookDTO.event().length());
        assertThrowsException(webHookDTO);
    }

    @Test
    public void eventExceedsChars_throwsException() {
        // Prepare
        //      objects
        WebHookDTO webHookDTO = new WebHookDTO(0L, "organizer", "0123456789012345678901234567891", "XC0DE", WebHookDTOSupportedAction.ORDER_APPROVED.getAction());
        //      methods

        // Act
        // Check
        System.out.println("String length: " + webHookDTO.event().length());
        assertThrowsException(webHookDTO);
    }

    @Test
    public void codeContainsSpecialChars_throwsException() {
        // Prepare
        //      objects
        WebHookDTO webHookDTO = new WebHookDTO(0L, "organizer", "some-event", "{<?>}", WebHookDTOSupportedAction.ORDER_APPROVED.getAction());
        //      methods

        // Act
        // Check
        System.out.println("String length: " + webHookDTO.code().length());
        assertThrowsException(webHookDTO);
    }

    @Test
    public void codeExceedsChars_throwsException() {
        // Prepare
        //      objects
        WebHookDTO webHookDTO = new WebHookDTO(0L, "organizer", "some-event", "123456", WebHookDTOSupportedAction.ORDER_APPROVED.getAction());
        //      methods

        // Act
        // Check
        System.out.println("String length: " + webHookDTO.code().length());
        assertThrowsException(webHookDTO);
    }

    private static void assertThrowsException(WebHookDTO webHookDTO) {
        WebHookDTOValidator webHookDTOValidator = new WebHookDTOValidator();
        assertThrows(WebHookDTONotValidException.class, () -> webHookDTOValidator.validateOrThrowException(webHookDTO));
    }
}