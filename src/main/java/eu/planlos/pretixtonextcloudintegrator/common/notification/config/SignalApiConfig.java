package eu.planlos.pretixtonextcloudintegrator.common.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.signal")
public record SignalApiConfig(
        boolean active,
        String address,
        String user,
        String password,
        String phoneSender,
        String phoneReceiver) {
}