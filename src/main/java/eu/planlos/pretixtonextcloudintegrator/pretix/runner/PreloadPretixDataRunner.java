package eu.planlos.pretixtonextcloudintegrator.pretix.runner;

import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixFeatureConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.ProductService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.BookingService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PreloadPretixDataRunner implements ApplicationRunner {

    private final PretixFeatureConfig pretixFeatureConfig;

    private final ProductService productService;
    private final QuestionService questionService;
    private final BookingService bookingService;

    public PreloadPretixDataRunner(PretixFeatureConfig pretixFeatureConfig, ProductService productService, QuestionService questionService, BookingService bookingService) {
        this.pretixFeatureConfig = pretixFeatureConfig;
        this.productService = productService;
        this.questionService = questionService;
        this.bookingService = bookingService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PreloadPretixDataRunner.class, args);
    }

    @Override
    public void run(ApplicationArguments arg0) {

        if(pretixFeatureConfig.preloadAllExceptOrdersEnabled()) {
            logSeparator("STARTING PRELOAD (NON-ORDERS)");
            questionService.fetchAll();
            productService.fetchAll();
            logSeparator("PRELOAD COMPLETE (NON-ORDERS)");
        }

        if(pretixFeatureConfig.preloadOrdersEnabled()) {
            logSeparator("STARTING PRELOAD (ORDERS)");
            bookingService.fetchAll();
            logSeparator("PRELOAD COMPLETE (ORDERS)");
        }
    }

    private static void logSeparator(String text) {
        log.info("##########################################################################");
        log.info("#   {}", text);
        log.info("##########################################################################");
    }
}