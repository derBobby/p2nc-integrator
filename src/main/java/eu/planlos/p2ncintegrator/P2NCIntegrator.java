package eu.planlos.p2ncintegrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ConfigurationPropertiesScan
@EntityScan(basePackages = {"eu.planlos.p2ncintegrator"})
@EnableJpaRepositories(basePackages = {"eu.planlos.p2ncintegrator"})
public class P2NCIntegrator {

    public static void main(String[] args) {
        SpringApplication.run(P2NCIntegrator.class, args);
    }
}