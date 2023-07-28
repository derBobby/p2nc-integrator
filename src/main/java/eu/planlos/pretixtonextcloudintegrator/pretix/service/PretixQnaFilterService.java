package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.common.util.GermanStringsUtility;
import eu.planlos.pretixtonextcloudintegrator.common.web.PretixContext;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixEventFilterConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PretixQnaFilterService {

    private final PretixEventFilterConfig pretixEventFilterConfig;
    protected final PretixContext pretixContext;

    public PretixQnaFilterService(PretixEventFilterConfig pretixEventFilterConfig, PretixContext pretixContext) {
        this.pretixEventFilterConfig = pretixEventFilterConfig;
        this.pretixContext = pretixContext;
        log.debug("Event filter config set in service");
    }

    protected Boolean filter(Map<Question, Answer> qnaMap) {

        String event = pretixContext.getEvent();

        // If no filter must be applied, then filter is successful
        List<PretixQnaFilter> pretixQnaFilterList = pretixEventFilterConfig.getQnaFilterForEvent(event);
        if (pretixQnaFilterList.isEmpty()) {
            return true;
        }

        for(PretixQnaFilter filter : pretixQnaFilterList) {

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

    public boolean irrelevantForBooking(Booking booking) {

        List<Position> ticketPositionList = booking.getPositionList().stream()
                .filter(p -> ! p.getProduct().getProductType().isAddon())
                .filter(p -> ! p.getQnA().isEmpty())
                .filter(p -> filter(p.getQnA()))
                .toList();
        return ticketPositionList.isEmpty();
    }

    private Map<String, String> extractQnaMap(Map<Question, Answer> qnaMap) {
        return qnaMap.entrySet().stream().collect(Collectors.toMap(entry -> GermanStringsUtility.handleGermanChars(entry.getKey().getText()), entry -> GermanStringsUtility.handleGermanChars(entry.getValue().getText())));
    }
}