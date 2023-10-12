package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixEventFilterConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PretixEventFilterService {

    private final List<PretixQnaFilter> pretixQnaFilterList = new ArrayList<>();
    private final PretixEventFilterConfig pretixEventFilterConfig;

    @Autowired
    public PretixEventFilterService(PretixEventFilterConfig pretixEventFilterConfig, ObjectMapper objectMapper) throws JsonProcessingException {
        this.pretixEventFilterConfig = pretixEventFilterConfig;
        pretixQnaFilterList.addAll(new StringToPretixQnaFilterConverter(objectMapper).convertAll(pretixEventFilterConfig.getFilterList()));
        log.debug("Event filter config set in service");
    }

    /**
     * Constructor package private for tests
     * @param pretixQnaFilterList Test filter list
     */
    PretixEventFilterService(List<PretixQnaFilter> pretixQnaFilterList) {
        this.pretixEventFilterConfig = new PretixEventFilterConfig(null, null);
        this.pretixQnaFilterList.addAll(pretixQnaFilterList);
    }

    public List<PretixQnaFilter> getQnaFilterFromPropertiesSource(String action, String event) {
        return pretixQnaFilterList.stream()
                .filter(filter -> filter.isForAction(action) && filter.isForEvent(event)).toList();
    }

    public boolean irrelevantForBooking(String action, Booking booking) {

        List<Position> ticketPositionList = booking.getPositionList().stream()
                .filter(position -> ! position.getProduct().getProductType().isAddon())
                .filter(position -> ! position.getQnA().isEmpty())
                .filter(position -> filter(action, booking.getEvent(), position.getQnA()))
                .toList();
        return ticketPositionList.isEmpty();
    }

    protected boolean filter(String action, String event, Map<Question, Answer> qnaMap) {

        if(pretixEventFilterConfig.isUserSourceConfigured()) {
            return filterByUserSource(action, event, qnaMap);
        }

        // Default in Config class, if nothing is provided
        return filterByPropertiesSource(action, event, qnaMap);
    }

    private boolean filterByPropertiesSource(String action, String event, Map<Question, Answer> qnaMap) {
        return getQnaFilterFromPropertiesSource(action, event)
                .stream()
                .anyMatch(filter -> filter.filterQnA(qnaMap));
    }

    //TODO continue here
    private Boolean filterByUserSource(String action, String event, Map<Question, Answer> qnaMap) {
        throw new IllegalArgumentException("Feature not yet available");
    }
}