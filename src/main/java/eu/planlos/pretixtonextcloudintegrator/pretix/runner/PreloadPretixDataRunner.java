package eu.planlos.pretixtonextcloudintegrator.pretix.runner;

import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixFeatureConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixBookingService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.ProductService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Component
public class PreloadPretixDataRunner implements ApplicationRunner {

    private final PretixFeatureConfig pretixFeatureConfig;
    private final PretixApiConfig pretixApiConfig;

    private final ProductService productService;
    private final QuestionService questionService;
    private final PretixBookingService pretixBookingService;

    public PreloadPretixDataRunner(PretixFeatureConfig pretixFeatureConfig, PretixApiConfig pretixApiConfig, ProductService productService, QuestionService questionService, PretixBookingService pretixBookingService) {
        this.pretixFeatureConfig = pretixFeatureConfig;
        this.pretixApiConfig = pretixApiConfig;
        this.productService = productService;
        this.questionService = questionService;
        this.pretixBookingService = pretixBookingService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PreloadPretixDataRunner.class, args);
    }

    @Override
    public void run(ApplicationArguments arg0) {
        if(pretixApiConfig.eventList() != null) {
            pretixApiConfig.eventList().forEach(this::preload);
        }
    }

    private void preload(String event) {
        if(pretixFeatureConfig.preloadAllExceptOrdersEnabled()) {
            logSeparator("STARTING PRELOAD (NON-ORDERS)");
            questionService.fetchAll(event);
            productService.fetchAll(event);
            logSeparator("PRELOAD COMPLETE (NON-ORDERS)");
        }
        if(pretixFeatureConfig.preloadOrdersEnabled()) {
            logSeparator("STARTING PRELOAD (ORDERS)");
            pretixBookingService.fetchAll(event);
            logSeparator("PRELOAD COMPLETE (ORDERS)");
        }
    }

    private static void logSeparator(String text) {
        log.info("##########################################################################");
        log.info("#   {}", text);
        log.info("##########################################################################");
    }
}