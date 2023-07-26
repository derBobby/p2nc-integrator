package eu.planlos.pretixtonextcloudintegrator.config;

import eu.planlos.pretixtonextcloudintegrator.model.QnaFilter;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@ConfigurationProperties(prefix = "event-filter")
public class EventFilterConfig {

    private final Map<String, List<Map<String, List<String>>>> map = new HashMap<>();
    private final Map<String, List<QnaFilter>> eventFilterMap = new HashMap<>();

    @ConstructorBinding
    public EventFilterConfig(Map<String, List<Map<String, List<String>>>> map) {
        this.map.putAll(map);
    }

    @PostConstruct
    // TODO directly create this QnaFilter object
    private void setup() {
        eventFilterMap.putAll(map.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().map(QnaFilter::new).toList()
        )));
        validate();
    }

    private void validate() {
        eventFilterMap.values().forEach(qnaList -> {
            Set<QnaFilter> set = new HashSet<>();
            for (QnaFilter qnaFilter : qnaList) {
                if (!set.add(qnaFilter)) {
                    throw new IllegalArgumentException("Filter is not unique");
                }
            }
        });
    }

    public List<QnaFilter> getQnaFilterForEvent(String event) {
        return eventFilterMap.getOrDefault(event, Collections.emptyList());
    }

    public static EventFilterConfig with(Map<String, List<QnaFilter>> eventFilterMap) {
        EventFilterConfig config = new EventFilterConfig();
        config.eventFilterMap.putAll(eventFilterMap);
        config.validate();
        return config;
    }
}