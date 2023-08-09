package eu.planlos.pretixtonextcloudintegrator.pretix.runner;

import eu.planlos.pretixtonextcloudintegrator.pretix.IPretixWebHookHandler;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixFeatureConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.WebHookDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebHookRunner implements ApplicationRunner {

    private final PretixFeatureConfig pretixFeatureConfig;
    private final PretixApiConfig pretixApiConfig;

    private final IPretixWebHookHandler webHookHandler;

    public WebHookRunner(PretixFeatureConfig pretixFeatureConfig, PretixApiConfig pretixApiConfig, IPretixWebHookHandler webHookHandler) {
        this.pretixFeatureConfig = pretixFeatureConfig;
        this.pretixApiConfig = pretixApiConfig;
        this.webHookHandler = webHookHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebHookRunner.class, args);
    }

    @Override
    public void run(ApplicationArguments arg0) {
        pretixApiConfig.eventList().forEach(this::sendHook);
    }

    private void sendHook(String event) {
        if (pretixFeatureConfig.sendDebugWebHookEnabled()) {
            WebHookDTO webHookDTO = new WebHookDTO(64158L, "kvkraichgau", event, "3PCGD", "pretix.event.order.approved");
            webHookHandler.handleUserCreation(webHookDTO.action(), webHookDTO.event(), webHookDTO.code());
        }
    }
}