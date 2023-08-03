package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "pretix.api")
public record PretixApiConfig(String address, String token, String organizer, List<String> eventList) {
}