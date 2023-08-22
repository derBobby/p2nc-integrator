package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixEventFilter;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "pretix.event-filter")
public class PretixEventFilterConfig {

    private final PretixEventFilterSource source;
    private final List<PretixEventFilter> pretixEventFilterList = new ArrayList<>();

    public PretixEventFilterConfig() {
        this.source = PretixEventFilterSource.PROPERTIES;
    }

    @ConstructorBinding
    public PretixEventFilterConfig(String source, List<PretixEventFilter> filters) {
        this.source = PretixEventFilterSource.fromString(source);
        this.pretixEventFilterList.addAll(filters);
    }

    @PostConstruct
    private void init() {
        this.pretixEventFilterList.isEmpty();
    }

    public PretixEventFilter getQnaFilterFromPropertiesSource(String action, String event) {
        return pretixEventFilterList.stream()
                .filter(filter -> filter.isForAction(action) && filter.isForEvent(event))
                .findFirst() // Use findFirst to get the first matching element or null if none match
                .orElse(null);
    }

    public static PretixEventFilterConfig with(PretixEventFilter pretixEventFilter) {
        PretixEventFilterConfig config = new PretixEventFilterConfig();
        config.pretixEventFilterList.add(pretixEventFilter);
        return config;
    }

    public boolean isPropertiesSourceConfigured() {
        return source.equals(PretixEventFilterSource.PROPERTIES);
    }

    public boolean isUserSourceConfigured() {
        return source.equals(PretixEventFilterSource.USER);
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