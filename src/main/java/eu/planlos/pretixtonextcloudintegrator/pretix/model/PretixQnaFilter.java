package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import eu.planlos.pretixtonextcloudintegrator.common.util.GermanStringsUtility;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Slf4j
@EqualsAndHashCode
@NoArgsConstructor
public final class PretixQnaFilter implements AttributeConverter<PretixQnaFilter, String> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Getter
    @Convert(converter = PretixQnaFilter.class)
    private final Map<String, List<String>> filterMap = new HashMap<>();

    public PretixQnaFilter(Map<String, List<String>> filterMap) {
        validateAnswerListUniqueEntries(filterMap.values());
        this.filterMap.putAll(filterMap);
    }

    private void validateAnswerListUniqueEntries(Collection<List<String>> values) {
        values.forEach(answerList -> {
            Set<String> testSet = new HashSet<>(answerList);
            if (testSet.size() != answerList.size()) {
                throw new IllegalArgumentException("Duplicate answer given");
            }
        });
    }

    public boolean filterQnA(Map<Question, Answer> qnaMap) {

        Map<String, String> extractedQnaMap = extractQnaMap(qnaMap);

        log.debug("Checking if map={}", qnaMap);
        log.debug("   matches filter={}", this);

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : filterMap.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            sb.append("'").append(key).append("': ");

            if (!values.isEmpty()) {
                for (int i = 0; i < values.size() - 1; i++) {
                    sb.append("'").append(values.get(i)).append("', ");
                }
                sb.append("'").append(values.get(values.size() - 1)).append("'");
            }
        }

        return sb.toString();
    }

    public static PretixQnaFilter fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String[] entries = text.split(";");
        Map<String, List<String>> filterMap = new HashMap<>();
        for (String entry : entries) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                String key = parts[0];
                List<String> values = Arrays.asList(parts[1].split(","));
                filterMap.put(key, values);
            }
        }
        return new PretixQnaFilter(filterMap);
    }

    @Override
    public String convertToDatabaseColumn(PretixQnaFilter pretixQnaFilter) {
        return toString();
    }

    @Override
    public PretixQnaFilter convertToEntityAttribute(String dbData) {
        return fromString(dbData);
    }
}