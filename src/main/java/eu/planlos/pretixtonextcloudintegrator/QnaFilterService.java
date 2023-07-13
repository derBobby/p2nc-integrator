package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.util.GermanStringsUtility;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Answer;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QnaFilterService {

    private final Map<String, List<String>> extractedFilterMap;

    public QnaFilterService(QnaFilterConfig qnaFilterConfig) {
        this.extractedFilterMap = qnaFilterConfig.qnaList().stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (list1, list2) -> {
                            log.error("Duplicate question found");
                            throw new IllegalArgumentException("Duplicate key encountered: " + list1);
                        }
                ));
        log.debug("Filter parameter for position qna set");
    }

    public Boolean filter(Map<Question, Answer> qnaMap) {
        Map<String, String> qnaExtractedMap = extractQnaMap(qnaMap);

        return extractedFilterMap.entrySet().stream()
                .anyMatch(entry -> {
                    String filterQuestion = entry.getKey();
                    List<String> filterAnswers = entry.getValue();

                    String answerText = qnaExtractedMap.get(filterQuestion);
                    return answerText != null && filterAnswers.contains(answerText);
                });
    }

    private Map<String, String> extractQnaMap(Map<Question, Answer> qnaMap) {
        return qnaMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> GermanStringsUtility.handleGermanChars(entry.getKey().getText()),
                        entry -> GermanStringsUtility.handleGermanChars(entry.getValue().getText())
                ));
    }
}