package eu.planlos.p2ncintegrator.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*
 * Reason for this class:
 * https://stackoverflow.com/a/72172859/7350955
 */
@Configuration
@ComponentScan(basePackages = "eu.planlos.javapretixconnector")
@EnableJpaRepositories(basePackages = {"eu.planlos.javapretixconnector"})
@EntityScan(basePackages = {"eu.planlos.javapretixconnector"})
public class PretixConfig {
}
