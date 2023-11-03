package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.planlos.pretixtonextcloudintegrator.common.util.GermanStringsUtility;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.ValidAction;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.ValidEvent;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.ValidFilterMap;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

import static eu.planlos.pretixtonextcloudintegrator.pretix.model.validation.FilterMapValidator.validateAnswerListsContainUniqueAnswers;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public final class PretixQnaFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @JsonProperty("action")
    @NotNull
    @ValidAction
    private String action;

    @JsonProperty("event")
    @NotNull
    @ValidEvent
    private String event;

    @JsonProperty("qna-list")
    @Convert(converter = PretixQnaFilterMapToStringDBConverter.class)
    @NotNull
    @ValidFilterMap
    private Map<String, List<String>> filterMap = new HashMap<>();

    /**
     * Constructor package private for tests
     * @param filterMap Map that consists of question and answers
     */
    PretixQnaFilter(Map<String, List<String>> filterMap) {
        this(null, null, filterMap);
    }

    public PretixQnaFilter(@NotNull String action, @NotNull String event, @NotNull Map<String, List<String>> filterMap) {
        validateAnswerListsContainUniqueAnswers(filterMap.values());
        this.action = action;
        this.event = event;
        this.filterMap.putAll(filterMap);
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