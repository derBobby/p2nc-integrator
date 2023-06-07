package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.pretix")
public record PretixApiConfig(String address, String token, String organizer, String event) {
}