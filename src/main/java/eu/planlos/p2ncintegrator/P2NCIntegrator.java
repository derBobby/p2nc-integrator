package eu.planlos.p2ncintegrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class P2NCIntegrator {

    public static void main(String[] args) {
        SpringApplication.run(P2NCIntegrator.class, args);
    }
}