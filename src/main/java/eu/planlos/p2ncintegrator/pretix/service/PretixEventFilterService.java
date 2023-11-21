package eu.planlos.p2ncintegrator.pretix.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.planlos.p2ncintegrator.pretix.config.PretixEventFilterConfig;
import eu.planlos.p2ncintegrator.pretix.model.*;
import eu.planlos.p2ncintegrator.pretix.repository.PretixQnaFilterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        //TODO test this switch
        if(pretixEventFilterConfig.isPropertiesSourceConfigured()) {
            pretixQnaFilterList.addAll(new StringToPretixQnaFilterConverter(objectMapper).convertAll(pretixEventFilterConfig.getFilterList()));
        }

        log.debug("Event QnA filter list configured in service");
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
     * User source methods
     */
    //TODO test
    //TODO only allowed is USER source
    public void addUserFilter(PretixQnaFilter pretixQnaFilter) {
        this.pretixQnaFilterList.add(pretixQnaFilter);
        this.pretixQnaFilterRepository.save(pretixQnaFilter);
    }

    //TODO test
    //TODO only allowed is USER source
    public void updateUserFilter(PretixQnaFilter pretixQnaFilter) {
        this.pretixQnaFilterList.stream()
                .filter(filter -> pretixQnaFilter.getId().equals(filter.getId()))
                .findAny()
                .map(x -> x.updateBy(pretixQnaFilter))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    //TODO delete

    //TODO test
    //TODO only allowed is USER source
    //TODO the elements in the list get the ID from repo save?
    public void addUserFilterList(List<PretixQnaFilter> pretixQnaFilterList) {
        this.pretixQnaFilterList.addAll(pretixQnaFilterList);
        this.pretixQnaFilterRepository.saveAll(pretixQnaFilterList);
    }

    //TODO test
    //TODO only allowed is USER source
    public Optional<PretixQnaFilter> get(Long id) {
        return pretixQnaFilterList.stream().filter(filter -> id.equals(filter.getId())).findFirst();
    }

    //TODO test
    //TODO only allowed is USER source
    public List<PretixQnaFilter> getAll() {
        return pretixQnaFilterList;
    }

    /*
     * Filtering methods
     */

    public boolean filterBookings(String action, Booking booking) {

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