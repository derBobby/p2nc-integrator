package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
    private String action;

    @Column
    private String event;

    @OneToMany
    private List<PretixQnaFilter> qnas = new ArrayList<>();

    public PretixEventFilter(String action, String event, List<PretixQnaFilter> qnas) {
        validateFilterListUniqueEntries(qnas);
        this.action = action;
        this.event = event;
        this.qnas.addAll(qnas);
    }

    public void addQna(PretixQnaFilter qnaFilter) {
        qnas.add(qnaFilter);
    }

    public boolean filter(Map<Question, Answer> qnaMap) {

        // If no filter must be applied, then filter is successful
        if (qnas.isEmpty()) {
            return true;
        }

        for (PretixQnaFilter filter : qnas) {

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

    public boolean isForAction(String action) {
        return this.action.equals(action);
    }

    public boolean isForEvent(String event) {
        return this.event.equals(event);
    }
}
