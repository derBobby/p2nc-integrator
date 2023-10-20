package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.planlos.pretixtonextcloudintegrator.common.util.GermanStringsUtility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
public final class PretixQnaFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @JsonProperty("action")
    private String action;

    @NotNull
    @JsonProperty("event")
    private String event;

    @NotNull
    @JsonProperty("qna-list")
    @Convert(converter = PretixQnaFilterMapToStringDBConverter.class)
    private Map<String, List<String>> filterMap = new HashMap<>();

    /**
     * Constructor package private for tests
     * @param filterMap Map that consists of question and answers
     */
    PretixQnaFilter(Map<String, List<String>> filterMap) {
        this(null, null, filterMap);
    }

    public PretixQnaFilter(@NotNull String action, @NotNull String event, @NotNull Map<String, List<String>> filterMap) {
        validateEachAnswerListConsistsOfUniqueAnswers(filterMap.values());
        this.action = action;
        this.event = event;
        this.filterMap.putAll(filterMap);
    }

    private void validateEachAnswerListConsistsOfUniqueAnswers(Collection<List<String>> values) {
        values.forEach(answerList -> {
            Set<String> testSet = new HashSet<>(answerList);
            if (testSet.size() != answerList.size()) {
                throw new IllegalArgumentException("Duplicate answer given");
            }
        });
    }

    public boolean isForAction(String action) {
        return this.action.equals(action);
    }

    public boolean isForEvent(String event) {
        return this.event.equals(event);
    }

    public boolean filterQnA(Map<Question, Answer> qnaMap) {

        Map<String, String> extractedQnaMap = extractQnaMap(qnaMap);

        return filterMap.entrySet().stream().allMatch(entry -> {
            String filterQuestion = entry.getKey();
            List<String> filterAnswerList = entry.getValue();
            String givenAnswer = extractedQnaMap.get(filterQuestion);
            return givenAnswer != null && filterAnswerList.contains(givenAnswer);
        });
    }

    private Map<String, String> extractQnaMap(Map<Question, Answer> qnaMap) {
        return qnaMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> GermanStringsUtility.handleGermanChars(entry.getKey().getText()),
                        entry -> GermanStringsUtility.handleGermanChars(entry.getValue().getText())));
    }
}