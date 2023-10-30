package eu.planlos.pretixtonextcloudintegrator.pretix.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.*;

public class FilterMapValidator implements ConstraintValidator<ValidFilterMap, Map<String, List<String>>> {

    @Override
    public void initialize(ValidFilterMap constraintAnnotation) {
    }

    @Override
    public boolean isValid(Map<String, List<String>> filterMap, ConstraintValidatorContext context) {
        if (filterMap == null) {
            return true;
        }

        if (filterMap.isEmpty()) {
            return false;
        }

        try {
            validateAnswerListsContainUniqueAnswers(filterMap.values());
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    public static void validateAnswerListsContainUniqueAnswers(Collection<List<String>> values) {
        values.forEach(answerList -> {
            Set<String> testSet = new HashSet<>(answerList);
            if (testSet.size() != answerList.size()) {
                throw new IllegalArgumentException("Duplicate answer given");
            }
        });
    }
}