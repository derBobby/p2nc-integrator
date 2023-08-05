package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixQnaFilter;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.*;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "pretix.event-filter")
public class PretixEventFilterConfig {

    @Getter
    private final PretixEventFilterSource pretixEventFilterSource;
    private final Map<String, List<Map<String, List<String>>>> internalEventFilterPropertiesMap = new HashMap<>();
    private final Map<String, List<PretixQnaFilter>> eventFilterPropertiesMap = new HashMap<>();

    public PretixEventFilterConfig() {
        this.pretixEventFilterSource = PretixEventFilterSource.PROPERTIES;
    }

    @ConstructorBinding
    public PretixEventFilterConfig(Map<String, List<Map<String, List<String>>>> map, String source) {
        this.internalEventFilterPropertiesMap.putAll(map);
        this.pretixEventFilterSource = PretixEventFilterSource.fromString(source);
    }

    @PostConstruct
    private void setup() {
        eventFilterPropertiesMap.putAll(internalEventFilterPropertiesMap.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().map(PretixQnaFilter::new).toList()
        )));
        validate();
    }

    private void validate() {
        eventFilterPropertiesMap.values().forEach(qnaList -> {
            Set<PretixQnaFilter> set = new HashSet<>();
            for (PretixQnaFilter pretixQnaFilter : qnaList) {
                if (!set.add(pretixQnaFilter)) {
                    throw new IllegalArgumentException("Filter is not unique");
                }
            }
        });
    }

    public List<PretixQnaFilter> getQnaFilterFromPropertiesSource(String event) {
        return eventFilterPropertiesMap.getOrDefault(event, Collections.emptyList());
    }

    public static PretixEventFilterConfig with(Map<String, List<PretixQnaFilter>> eventFilterMap) {
        PretixEventFilterConfig config = new PretixEventFilterConfig();
        config.eventFilterPropertiesMap.putAll(eventFilterMap);
        config.validate();
        return config;
    }

    public boolean isPropertiesSourceConfigured() {
        return pretixEventFilterSource.equals(PretixEventFilterSource.PROPERTIES);
    }

    public boolean isUserSourceConfigured() {
        return pretixEventFilterSource.equals(PretixEventFilterSource.USER);
    }

    @Getter
    public enum PretixEventFilterSource {
        PROPERTIES("properties"),
        USER("user");

        private final String value;

        PretixEventFilterSource(String value) {
            this.value = value;
        }

        public static PretixEventFilterSource fromString(String text) {

            if(text.isEmpty()) {
                return PretixEventFilterSource.PROPERTIES;
            }

            for (PretixEventFilterSource source : PretixEventFilterSource.values()) {
                if (source.value.equalsIgnoreCase(text)) {
                    return source;
                }
            }

            throw new IllegalArgumentException("pretix.event-filter.source must be set to one of the values: " + validSources());
        }

        private static String validSources() {
            return Arrays.stream(PretixEventFilterSource.values())
                    .map(PretixEventFilterSource::getValue)
                    .collect(Collectors.joining(", "));        }
    }
}