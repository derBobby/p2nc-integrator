package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.nextcloud")
public record NextcloudApiConfig(
        String address,
        String user,
        String password) {
}