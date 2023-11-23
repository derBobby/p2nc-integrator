package eu.planlos.p2ncintegrator.pretix.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.planlos.p2ncintegrator.pretix.config.PretixEventFilterConfig;
import eu.planlos.p2ncintegrator.pretix.model.*;
import eu.planlos.p2ncintegrator.pretix.repository.PretixQnaFilterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class PretixEventFilterService {

    private final PretixEventFilterConfig pretixEventFilterConfig;
    private final PretixQnaFilterRepository pretixQnaFilterRepository;

    @Autowired
    public PretixEventFilterService(PretixEventFilterConfig pretixEventFilterConfig, ObjectMapper objectMapper, PretixQnaFilterRepository pretixQnaFilterRepository) throws JsonProcessingException {
        this.pretixEventFilterConfig = pretixEventFilterConfig;
        this.pretixQnaFilterRepository = pretixQnaFilterRepository;

        //TODO test this switch
        if(pretixEventFilterConfig.isPropertiesSourceConfigured()) {
            pretixQnaFilterRepository.saveAll(new StringToPretixQnaFilterConverter(objectMapper).convertAll(pretixEventFilterConfig.getFilterList()));
        }

        log.debug("Event QnA filter list configured in service");
    }

    /**
     * Constructor package private for tests
     * @param pretixQnaFilterList Test filter list
     */
    PretixEventFilterService(PretixQnaFilterRepository pretixQnaFilterRepository, List<PretixQnaFilter> pretixQnaFilterList) {
        this.pretixEventFilterConfig = new PretixEventFilterConfig(null, null);
        this.pretixQnaFilterRepository = pretixQnaFilterRepository;
        this.pretixQnaFilterRepository.saveAll(pretixQnaFilterList);
    }

    /*
     * User source methods
     */

    //TODO test
    /**
     * Creates filter if it exists or not
     * Not idempotent repo access, will create new for each call
     * @param pretixQnaFilter Filter object
     */
    public void addFilter(PretixQnaFilter pretixQnaFilter) {
        if(pretixQnaFilter.getId() == null) {
            this.pretixQnaFilterRepository.save(pretixQnaFilter);
            return;
        }
        throw new IllegalArgumentException("Filter must not have id");
    }

    //TODO test
    public Optional<PretixQnaFilter> getFilter(Long id) {
        return this.pretixQnaFilterRepository.findById(id);
    }

    //TODO test
    public List<PretixQnaFilter> getAll() {
        return pretixQnaFilterRepository.findAll();
    }

    //TODO test
    /**
     * Creates or updates filter.
     * Idempotent method.
     * @param pretixQnaFilter Filter object
     */
    public void updateFilter(PretixQnaFilter pretixQnaFilter) {
        if(pretixQnaFilter.getId() != null) {
            this.pretixQnaFilterRepository.save(pretixQnaFilter);
            return;
        }
        throw new IllegalArgumentException("Filter must have have id");
    }

    //TODO test
    public void deleteUserFilter(Long id) {
        if(getFilter(id).isPresent()) {
            this.pretixQnaFilterRepository.deleteById(id);
            return;
        }
        throw new EntityNotFoundException("Filter does not exist id=" + id);
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

        return pretixQnaFilterRepository.findByActionAndEvent(action, event).stream()
                .anyMatch(filter -> filter.filterQnA(qnaMap));
    }
}