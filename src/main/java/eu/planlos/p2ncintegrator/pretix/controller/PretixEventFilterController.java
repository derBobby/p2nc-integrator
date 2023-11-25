package eu.planlos.p2ncintegrator.pretix.controller;

import eu.planlos.p2ncintegrator.pretix.model.PretixQnaFilter;
import eu.planlos.p2ncintegrator.pretix.model.dto.PretixQnaFilterCreateDTO;
import eu.planlos.p2ncintegrator.pretix.model.dto.PretixQnaFilterUpdateDTO;
import eu.planlos.p2ncintegrator.pretix.service.PretixEventFilterService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(PretixEventFilterController.URL_FILTER)
@Slf4j
public class PretixEventFilterController {

    public static final String URL_FILTER = "/api/v1/filter";

    private final PretixEventFilterService pretixEventFilterService;

    public PretixEventFilterController(PretixEventFilterService pretixEventFilterService) {
        this.pretixEventFilterService = pretixEventFilterService;
    }

    /*
     * CRUD
     */

    //TODO test
    /**
     * cURL example:
     * curl -v -s -X POST -H "Content-Type: application/json;charset=UTF-8" -d '{"action":"pretix.event.order.approved","event":"event","qna-list":{"test":["test"]}}' http://localhost:8080/api/v1/filter
     * @param pretixQnaFilterDTO Transfer object, will be converted to business object
     * @return JSON representation
     */
    @PostMapping
    public ResponseEntity<PretixQnaFilter> post(@Valid @RequestBody PretixQnaFilterCreateDTO pretixQnaFilterDTO) {
        PretixQnaFilter pretixQnaFilter = new PretixQnaFilter(pretixQnaFilterDTO);
        log.info("Create filter={}", pretixQnaFilter);
        pretixEventFilterService.addFilter(pretixQnaFilter);
        log.info("Created filter id={}", pretixQnaFilter.getId());

        return ResponseEntity.ok().body(pretixQnaFilter);
    }

    //TODO test
    /**
     * curl -s -X GET http://localhost:8080/api/v1/filter/1
     * @return JSON representation
     */
    @GetMapping
    public ResponseEntity<List<PretixQnaFilter>> getAll() {
        log.info("Read all filters");
        return ResponseEntity.ok().body(pretixEventFilterService.getAllFilters());
    }

    //TODO test
    /**
     * cURL example:
     * curl -s -X GET http://localhost:8080/api/v1/filter/1
     * @param id of the filter
     * @return PretixQnaFilter.class as JSON
     */
    @GetMapping("/{id}")
    public ResponseEntity<PretixQnaFilter> get(@PathVariable Long id) {
        log.info("Read filter id={}", id);
        Optional<PretixQnaFilter> optionalPretixQnaFilter = pretixEventFilterService.getFilter(id);
        return optionalPretixQnaFilter
                .map(pretixQnaFilter -> ResponseEntity.ok().body(pretixQnaFilter))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //TODO test
    @PutMapping
    public ResponseEntity<PretixQnaFilter> put(@Valid @RequestBody PretixQnaFilterUpdateDTO pretixQnaFilterDTO) {
        PretixQnaFilter pretixQnaFilter = new PretixQnaFilter(pretixQnaFilterDTO);
        log.info("Update filter={}", pretixQnaFilter);
        pretixEventFilterService.updateFilter(pretixQnaFilter);
        log.info("Updated filter id={}", pretixQnaFilter.getId());
        return ResponseEntity.ok().body(pretixQnaFilter);
    }

    //TODO test
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        log.info("Delete filter id={}", id);
        pretixEventFilterService.deleteFilter(id);
        log.info("Deleted filter id={}", id);
    }
}