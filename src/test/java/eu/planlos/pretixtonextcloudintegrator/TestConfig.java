package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.common.notification.config.SignalApiConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@TestConfiguration
public class TestConfig {

    @Bean
    public SignalApiConfig signalApiConfig() {
        return new SignalApiConfig(false, "testAddress", "testUser", "testPassword", "testSender", "testReceiver");
    }

    @Bean
    public PretixApiConfig pretixApiConfig() {
        return new PretixApiConfig(false, "testAddress", "testToken", "testOrganizer", new ArrayList<>());
    }
}
