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

        if(pretixEventFilterConfig.isPropertiesSourceConfigured()) {
            log.debug("Filters provided by properties file are configured");
            handlePropertiesAsFilterSource(objectMapper);
            return;
        }
        log.debug("Filters provided by user are configured");
    }

    private void handlePropertiesAsFilterSource(ObjectMapper objectMapper) throws JsonProcessingException {
        if(pretixQnaFilterRepository.findAll().isEmpty()) {
            log.debug("No filters in DB found. Proceed to persist filters from properties file");
            persistFilterFromProperties(objectMapper);
        } else {
            throw new IllegalArgumentException("Config file is configured as source for filters, but filters already exist in DB. Clean DB or switch to source USER");
        }
    }

    private void persistFilterFromProperties(ObjectMapper objectMapper) throws JsonProcessingException {
        pretixQnaFilterRepository.saveAll(
                new StringToPretixQnaFilterConverter(objectMapper)
                        .convertAll(pretixEventFilterConfig.getFilterList()));
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

    /**
     * Creates filter if it exists or not
     * Not idempotent repo access, will create new for each call
     * @param pretixQnaFilter Filter object
     */
    public void addFilter(PretixQnaFilter pretixQnaFilter) {
        if(pretixQnaFilter.getId() == null) {
            pretixQnaFilterRepository.save(pretixQnaFilter);
            return;
        }
        throw new IllegalArgumentException("Filter must not have id");
    }

    public Optional<PretixQnaFilter> getFilter(Long id) {
        return pretixQnaFilterRepository.findById(id);
    }

    public List<PretixQnaFilter> getAllFilters() {
        return pretixQnaFilterRepository.findAll();
    }

    /**
     * Creates or updates filter.
     * Idempotent method.
     * @param pretixQnaFilter Filter object
     */
    public void updateFilter(PretixQnaFilter pretixQnaFilter) {
        if(pretixQnaFilter.getId() == null) {
            throw new IllegalArgumentException("Filter must have have id");
        }
        if(! pretixQnaFilterRepository.existsById(pretixQnaFilter.getId())) {
            throw new EntityNotFoundException("Filter does not exist id=" + pretixQnaFilter.getId());
        }
        pretixQnaFilterRepository.save(pretixQnaFilter);
    }

    public void deleteFilter(Long id) {
        if(getFilter(id).isPresent()) {
            pretixQnaFilterRepository.deleteById(id);
            return;
        }
        throw new EntityNotFoundException("Filter does not exist id=" + id);
    }

    /*
     * Filtering methods
     */

    /**
     * Looks if filter exists for given action-booking that is interested in it.
     * @param action to be looked for
     * @param booking to be looked for
     * @return true if there is no  filter at all, that wants the booking
     */
    public boolean bookingNotWantedByAnyFilter(String action, Booking booking) {
        List<Position> ticketPositionList = booking.getPositionList().stream()
                .filter(position -> ! position.getProduct().getProductType().isAddon())
                .filter(position -> ! position.getQnA().isEmpty()) //TODO could be removed?
                .filter(position -> matchesQnaFilter(action, booking.getEvent(), position.getQnA()))
                .toList();
        return ticketPositionList.isEmpty();
    }

    /**
     * Check if matching filter exists in database
     * Not private for tests.
     * @param action to be looked for
     * @param event to be looked for
     * @param qnaMap to be looked for
     * @return true if a filter exists
     */
    boolean matchesQnaFilter(String action, String event, Map<Question, Answer> qnaMap) {
        return pretixQnaFilterRepository.findByActionAndEvent(action, event).stream()
                .anyMatch(filter -> filter.filterQnA(qnaMap));
    }
}