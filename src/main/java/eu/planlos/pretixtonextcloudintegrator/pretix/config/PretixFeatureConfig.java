package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

@ConfigurationProperties(prefix = "api.pretix.feature")
public record PretixFeatureConfig(Boolean preloadAllExceptOrdersEnabled, Boolean preloadOrdersEnabled) {

    @ConstructorBinding
    public PretixFeatureConfig(Boolean preloadAllExceptOrdersEnabled, Boolean preloadOrdersEnabled) {
        this.preloadAllExceptOrdersEnabled = Optional.ofNullable(preloadAllExceptOrdersEnabled).orElse(false);
        this.preloadOrdersEnabled = Optional.ofNullable(preloadOrdersEnabled).orElse(false);
    }
}