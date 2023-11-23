package eu.planlos.p2ncintegrator.pretix.model.validation;

import eu.planlos.p2ncintegrator.pretix.PretixTestDataUtility;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.EMPTY_MAP;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class FilterMapValidatorTest extends PretixTestDataUtility {

    FilterMapValidator validator = new FilterMapValidator();

    @Test
    public void nullList_isValid() {
        assertTrue(validator.isValid(null));

    }

    @Test
    public void emptyList_isValid() {
        assertTrue(validator.isValid(EMPTY_MAP));
    }

    @Test
    public void duplicateAnswer_isInvalid() {
        assertFalse(validator.isValid(
                Map.of(
                        "Question!",
                        List.of("Answer!", "Answer!")
                ))
        );
    }

    @Test
    public void differentAnswer_isInvalid() {
        assertTrue(validator.isValid(
                Map.of(
                        "Question!",
                        List.of("Answer 1!", "Answer 2!")
                ))
        );
    }
}