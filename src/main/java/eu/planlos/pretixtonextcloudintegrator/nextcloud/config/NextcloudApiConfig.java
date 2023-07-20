package eu.planlos.pretixtonextcloudintegrator.nextcloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.nextcloud")
public record NextcloudApiConfig(Boolean active, String address, String user, String password, String defaultGroup) {
    public Boolean inactive() {
        return !active;
    }
}