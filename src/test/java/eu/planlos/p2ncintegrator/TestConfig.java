package eu.planlos.p2ncintegrator;

import eu.planlos.javapretixconnector.config.PretixApiConfig;
import eu.planlos.javapretixconnector.config.PretixFeatureConfig;
import eu.planlos.p2ncintegrator.common.notification.config.SignalApiConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;

@TestConfiguration
@ComponentScan(basePackages = {"eu.planlos.p2ncintegrator", "eu.planlos.javapretixconnector"})
public class TestConfig {

    @Bean
    public SignalApiConfig signalApiConfig() {
        return new SignalApiConfig(false, "testAddress", "testUser", "testPassword", "testSender", "testReceiver", 0, 0);
    }

    @Bean
    public PretixApiConfig pretixApiConfig() {
        return new PretixApiConfig(false, "testAddress", "testToken", "testOrganizer", new ArrayList<>(), 0, 0);
    }

    @Bean
    public PretixFeatureConfig pretixFeatureConfig() {
        return new PretixFeatureConfig(false, false);
    }
}
