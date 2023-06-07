package eu.planlos.pretixtonextcloudintegrator.nextcloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.nextcloud")
public record NextcloudApiConfig(String address, String user, String password, String defaultGroup) {
}