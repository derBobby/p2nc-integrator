package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixQnaFilter;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@ConfigurationProperties(prefix = "event-filter")
public class PretixEventFilterConfig {

    private final Map<String, List<Map<String, List<String>>>> map = new HashMap<>();
    private final Map<String, List<PretixQnaFilter>> eventFilterMap = new HashMap<>();

    @ConstructorBinding
    public PretixEventFilterConfig(Map<String, List<Map<String, List<String>>>> map) {
        this.map.putAll(map);
    }

    @PostConstruct
    // TODO directly create this QnaFilter object
    private void setup() {
        eventFilterMap.putAll(map.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().map(PretixQnaFilter::new).toList()
        )));
        validate();
    }

    private void validate() {
        eventFilterMap.values().forEach(qnaList -> {
            Set<PretixQnaFilter> set = new HashSet<>();
            for (PretixQnaFilter pretixQnaFilter : qnaList) {
                if (!set.add(pretixQnaFilter)) {
                    throw new IllegalArgumentException("Filter is not unique");
                }
            }
        });
    }

    public List<PretixQnaFilter> getQnaFilterForEvent(String event) {
        return eventFilterMap.getOrDefault(event, Collections.emptyList());
    }

    public static PretixEventFilterConfig with(Map<String, List<PretixQnaFilter>> eventFilterMap) {
        PretixEventFilterConfig config = new PretixEventFilterConfig();
        config.eventFilterMap.putAll(eventFilterMap);
        config.validate();
        return config;
    }
}