package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.util.GermanStringsUtility;
import eu.planlos.pretixtonextcloudintegrator.config.EventFilterConfig;
import eu.planlos.pretixtonextcloudintegrator.model.QnaFilter;
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

    private final EventFilterConfig eventFilterConfig;

    public QnaFilterService(EventFilterConfig eventFilterConfig) {
        this.eventFilterConfig = eventFilterConfig;
        log.debug("Event filter config set in service");
    }

    public Boolean filter(String event, Map<Question, Answer> qnaMap) {

        // If no filter must be applied, then filter is successful
        List<QnaFilter> qnaFilterList = eventFilterConfig.getQnaFilterForEvent(event);
        if (qnaFilterList.isEmpty()) {
            return true;
        }

        for(QnaFilter filter : qnaFilterList) {

            Map<String, List<String>> qnaFilterMap = filter.filterMap();
            Map<String, String> extractedQnaMap = extractQnaMap(qnaMap);

            log.debug("Checking if map={}", extractedQnaMap);
            log.debug("   matches filter={}", filter);

            boolean matches = qnaFilterMap.entrySet().stream().allMatch(entry -> {
                String filterQuestion = entry.getKey();
                List<String> filterAnswerList = entry.getValue();
                String givenAnswer = extractedQnaMap.get(filterQuestion);
                return givenAnswer != null && filterAnswerList.contains(givenAnswer);
            });

            if(matches) {
                log.debug("   Filter matches!");
                return true;
            }
            log.debug("   Filter matches NOT, continue with next one");
        }
        log.debug("   No filter matches");
        return false;
    }

    private Map<String, String> extractQnaMap(Map<Question, Answer> qnaMap) {
        return qnaMap.entrySet().stream().collect(Collectors.toMap(entry -> GermanStringsUtility.handleGermanChars(entry.getKey().getText()), entry -> GermanStringsUtility.handleGermanChars(entry.getValue().getText())));
    }
}