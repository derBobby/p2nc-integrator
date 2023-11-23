package eu.planlos.p2ncintegrator.pretix.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.*;

public class FilterMapValidator implements ConstraintValidator<ValidFilterMap, Map<String, List<String>>> {

    @Override
    public void initialize(ValidFilterMap constraintAnnotation) {
    }

    /**
     * Used for tests.
     * @param filterMap Map of questions and answers
     * @return true if valid
     */
    boolean isValid(Map<String, List<String>> filterMap) {
        return isValid(filterMap, null);
    }

    /**
     * Checks questions ans answers for validity. Used by Annotations.
     * @param filterMap Map of questions and answers
     * @param context Not necessary here.
     * @return true if valid
     */
    @Override
    public boolean isValid(Map<String, List<String>> filterMap, ConstraintValidatorContext context) {
        if (filterMap == null || filterMap.isEmpty()) {
            return true;
        }

        try {
            validateAnswerListsContainUniqueAnswers(filterMap.values());
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    /**
     * Required for constructors on startup
     * @param values List of answers for a filter
     */
    public static void validateAnswerListsContainUniqueAnswers(Collection<List<String>> values) {
        values.forEach(answerList -> {
            Set<String> testSet = new HashSet<>(answerList);
            if (testSet.size() != answerList.size()) {
                throw new IllegalArgumentException("Duplicate answer given");
            }
        });
    }
}