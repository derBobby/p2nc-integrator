package eu.planlos.p2ncintegrator.pretix.controller;

import eu.planlos.p2ncintegrator.pretix.model.PretixQnaFilter;
import eu.planlos.p2ncintegrator.pretix.model.dto.PretixQnaFilterDTO;
import eu.planlos.p2ncintegrator.pretix.service.PretixEventFilterService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

    //TODO return JSON
    //TODO continue: update, delete,
    //TODO test
    /**
     * cURL example:
     * curl -v -s -X POST -H "Content-Type: application/json;charset=UTF-8" -d '{"action":"pretix.event.order.approved","event":"event","qna-list":{"test":["test"]}}' http://localhost:8080/api/v1/filter
     * @param pretixQnaFilterDTO Transfer object, will be converted to business object
     * @return JSON representation
     */
    @PostMapping
    public ResponseEntity<String> webHook(@Valid @RequestBody PretixQnaFilterDTO pretixQnaFilterDTO) {

        PretixQnaFilter pretixQnaFilter = new PretixQnaFilter(pretixQnaFilterDTO);

        log.info("Incoming filter={}", pretixQnaFilter);

        pretixEventFilterService.addUserFilter(pretixQnaFilter);

        log.info("Filter saved with id={}", pretixQnaFilter.getId());

        return ResponseEntity.ok().body(pretixQnaFilter.toString());
    }

    //TODO return JSON
    //TODO test
    /**
     * curl -s -X GET http://localhost:8080/api/v1/filter/1
     * @return JSON representation
     */
    @GetMapping
    public List<PretixQnaFilter> getAll() {
        return pretixEventFilterService.getAll();
    }

    //TODO return JSON
    //TODO test
    /**
     * cURL example:
     * curl -s -X GET http://localhost:8080/api/v1/filter/1
     * @param id of the filter
     * @return JSON representation
     */
    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable Long id) {

        Optional<PretixQnaFilter> optionalPretixQnaFilter = pretixEventFilterService.get(id);
        return optionalPretixQnaFilter
                .map(pretixQnaFilter -> ResponseEntity.ok().body(pretixQnaFilter.toString()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}