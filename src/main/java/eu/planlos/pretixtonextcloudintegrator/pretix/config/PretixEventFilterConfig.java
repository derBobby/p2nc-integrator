package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixQnaFilter;
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
    private final List<PretixQnaFilter> filterList = new ArrayList<>();

    public PretixEventFilterConfig() {
        this.source = PretixEventFilterSource.PROPERTIES;
    }

    @ConstructorBinding
    public PretixEventFilterConfig(String source, List<PretixQnaFilter> filterList) {
        this.source = PretixEventFilterSource.fromString(source);
        this.filterList.addAll(filterList);
    }

    public static PretixEventFilterConfig with(List<PretixQnaFilter> filterList) {
        PretixEventFilterConfig config = new PretixEventFilterConfig();
        config.filterList.addAll(filterList);
        return config;
    }

    public List<PretixQnaFilter> getQnaFilterFromPropertiesSource(String action, String event) {
        return filterList.stream()
                .filter(filter -> filter.isForAction(action) && filter.isForEvent(event)).toList();
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