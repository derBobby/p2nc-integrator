package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixEventFilterConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PretixEventFilterService {

    private final PretixEventFilterConfig pretixEventFilterConfig;

    public PretixEventFilterService(PretixEventFilterConfig pretixEventFilterConfig) {
        this.pretixEventFilterConfig = pretixEventFilterConfig;
        log.debug("Event filter config set in service");
    }

    public boolean irrelevantForBooking(Booking booking) {

        List<Position> ticketPositionList = booking.getPositionList().stream()
                .filter(position -> ! position.getProduct().getProductType().isAddon())
                .filter(position -> ! position.getQnA().isEmpty())
                .filter(position -> filter(booking.getEvent(), position.getQnA()))
                .toList();
        return ticketPositionList.isEmpty();
    }

    protected Boolean filter(String event, Map<Question, Answer> qnaMap) {

        if(pretixEventFilterConfig.isUserSourceConfigured()) {
            return filterByUserSource(event, qnaMap);
        }

        // Is default config if nothing is provided
        return filterByPropertiesSource(event, qnaMap);
    }

    //TODO continue here
    private Boolean filterByUserSource(String event, Map<Question, Answer> qnaMap) {
        throw new IllegalArgumentException("Feature not yet available");
    }

    private Boolean filterByPropertiesSource(String event, Map<Question, Answer> qnaMap) {
        PretixEventFilter pretixEventFilter = pretixEventFilterConfig.getQnaFilterFromPropertiesSource(event);
        return pretixEventFilter.filter(qnaMap);
    }
}