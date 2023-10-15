package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixEventFilterConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.*;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.PretixQnaFilterRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PretixEventFilterService {

    private final List<PretixQnaFilter> pretixQnaFilterList = new ArrayList<>();
    private final PretixEventFilterConfig pretixEventFilterConfig;
    private final PretixQnaFilterRepository pretixQnaFilterRepository;

    @Autowired
    public PretixEventFilterService(PretixEventFilterConfig pretixEventFilterConfig, ObjectMapper objectMapper, PretixQnaFilterRepository pretixQnaFilterRepository) throws JsonProcessingException {
        this.pretixEventFilterConfig = pretixEventFilterConfig;
        this.pretixQnaFilterRepository = pretixQnaFilterRepository;
        pretixQnaFilterList.addAll(new StringToPretixQnaFilterConverter(objectMapper).convertAll(pretixEventFilterConfig.getFilterList()));
        log.debug("Event QnA filter list configured in service");
    }

    //TODO write test for persisting
    @PostConstruct
    @Profile(value = "DEVELOPMENT")
    public void persist() {
        pretixQnaFilterRepository.saveAll(pretixQnaFilterList);
        log.debug("Event QnA filter list saved in database");
        List<PretixQnaFilter> pretixQnaFilterDBList = pretixQnaFilterRepository.findAll();
        log.debug("Event QnA filter list loaded from database: {}", pretixQnaFilterDBList);
    }

    /**
     * Constructor package private for tests
     * @param pretixQnaFilterList Test filter list
     */
    PretixEventFilterService(List<PretixQnaFilter> pretixQnaFilterList) {
        this.pretixEventFilterConfig = new PretixEventFilterConfig(null, null);
        this.pretixQnaFilterList.addAll(pretixQnaFilterList);
        this.pretixQnaFilterRepository = null;
    }

    /*
     * Filtering methods
     */

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

    //TODO continue here
    private Boolean filterByUserSource(String action, String event, Map<Question, Answer> qnaMap) {
        throw new IllegalArgumentException("Feature not yet available");
    }

    private boolean filterByPropertiesSource(String action, String event, Map<Question, Answer> qnaMap) {
        return pretixQnaFilterList.stream()
                .filter(filter -> filter.isForAction(action) && filter.isForEvent(event))
                .anyMatch(filter -> filter.filterQnA(qnaMap));
    }
}