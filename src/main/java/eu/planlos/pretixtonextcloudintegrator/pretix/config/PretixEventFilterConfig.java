package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixEventFilter;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixQnaFilter;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.*;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "pretix.event-filter")
public class PretixEventFilterConfig {

    private final Map<String, List<Map<String, List<String>>>> internalEventFilterPropertiesMap = new HashMap<>();
    @Getter
    private final PretixEventFilterSource pretixEventFilterSource;
    private final List<PretixEventFilter> pretixEventFilterList = new ArrayList<>();

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
        internalEventFilterPropertiesMap.forEach((key, value) ->
                pretixEventFilterList.add(new PretixEventFilter(key, value.stream()
                        .map(PretixQnaFilter::new)
                        .toList()))
        );
    }

    public PretixEventFilter getQnaFilterFromPropertiesSource(String event) {
        return pretixEventFilterList.stream()
                .filter(filter -> filter.getEvent().equals(event))
                .findFirst() // Use findFirst to get the first matching element or null if none match
                .orElse(null);
    }

    public static PretixEventFilterConfig with(PretixEventFilter pretixEventFilter) {
        PretixEventFilterConfig config = new PretixEventFilterConfig();
        config.pretixEventFilterList.add(pretixEventFilter);
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