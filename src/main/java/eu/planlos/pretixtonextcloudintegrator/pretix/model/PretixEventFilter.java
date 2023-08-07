package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Entity
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Slf4j
public class PretixEventFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @Getter
    private String event;

    @OneToMany
    private List<PretixQnaFilter> pretixQnaFilterList = new ArrayList<>();

    public PretixEventFilter(String event, List<PretixQnaFilter> pretixQnaFilterList) {
        validateFilterListUniqueEntries(pretixQnaFilterList);
        this.event = event;
        this.pretixQnaFilterList.addAll(pretixQnaFilterList);
    }

    public boolean filter(Map<Question, Answer> qnaMap) {

        // If no filter must be applied, then filter is successful
        if (pretixQnaFilterList.isEmpty()) {
            return true;
        }

        for (PretixQnaFilter filter : pretixQnaFilterList) {

            boolean matches = filter.filterQnA(qnaMap);

            if (matches) {
                log.debug("   Filter matches!");
                return true;
            }
            log.debug("   Filter matches NOT, continue with next one");
        }
        log.debug("   No filter matches");
        return false;
    }

    private void validateFilterListUniqueEntries(List<PretixQnaFilter> pretixQnaFilterList) {
        Set<Map<String, List<String>>> uniqueFilterMaps = new HashSet<>();

        for (PretixQnaFilter filter : pretixQnaFilterList) {
            Map<String, List<String>> filterMap = filter.getFilterMap();
            if (!uniqueFilterMaps.add(filterMap)) {
                String message = String.format("Duplicate filter found. Hint: %s", filterMap);
                log.info(message);
                throw new IllegalArgumentException(message);
            }
        }
    }
}
