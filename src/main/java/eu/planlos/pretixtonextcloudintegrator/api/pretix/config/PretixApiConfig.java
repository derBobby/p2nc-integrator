package eu.planlos.pretixtonextcloudintegrator.api.pretix.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.pretix")
public record PretixApiConfig(
        String address,
        String apiToken,
        String organizer,
        String event) {
}