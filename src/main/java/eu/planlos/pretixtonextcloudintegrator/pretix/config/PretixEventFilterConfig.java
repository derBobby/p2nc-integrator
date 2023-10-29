package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ConfigurationProperties(prefix = "pretix.event-filter")
public class PretixEventFilterConfig {

    private final PretixEventFilterSource source;

    @Getter
    private final List<String> filterList;

    public PretixEventFilterConfig(
            String source,
            List<String> filterList) {
        this.source = PretixEventFilterSource.fromString(source);
        this.filterList = filterList;

        log.info("Creating pretix event filter config:");
        log.info("- filter source: {}", source);
    }

    public boolean isPropertiesSourceConfigured() {
        return source.equals(PretixEventFilterSource.PROPERTIES);
    }

    public boolean isUserSourceConfigured() {
        return source.equals(PretixEventFilterSource.USER);
    }

    @Getter
    private enum PretixEventFilterSource {
        PROPERTIES("properties"),
        USER("user");

        private final String value;

        PretixEventFilterSource(String value) {
            this.value = value;
        }

        public static PretixEventFilterSource fromString(String text) {

            if(text == null || text.isEmpty()) {
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