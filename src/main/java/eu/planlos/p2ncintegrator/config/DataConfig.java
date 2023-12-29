package eu.planlos.p2ncintegrator.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "eu.planlos.javapretixconnector",
        "eu.planlos.javaspringwebutilities",
        "eu.planlos.p2ncintegrator"
})
@EntityScan(basePackages = {
        "eu.planlos.javapretixconnector",
        "eu.planlos.javaspringwebutilities",
        "eu.planlos.p2ncintegrator"
})
public class DataConfig {
}
