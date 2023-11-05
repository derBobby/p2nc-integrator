package eu.planlos.pretixtonextcloudintegrator.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/*
 * Reason for this class:
 * https://stackoverflow.com/a/72172859/7350955
 */
@Configuration
@ComponentScan(basePackages = "eu.planlos.javanextcloudconnector")
public class NextcloudConfig {
}
