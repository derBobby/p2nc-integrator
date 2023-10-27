package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import java.util.List;

@ConfigurationProperties(prefix = "pretix.api")
@Profile("!TEST")
public record PretixApiConfig(boolean active, String address, String token, String organizer, List<String> eventList) {
    public boolean inactive() {
        return !active;
    }
}