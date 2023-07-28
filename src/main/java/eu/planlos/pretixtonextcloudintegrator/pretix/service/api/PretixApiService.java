package eu.planlos.pretixtonextcloudintegrator.pretix.service.api;

import eu.planlos.pretixtonextcloudintegrator.pretix.service.PretixContext;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public abstract class PretixApiService {

    protected final PretixApiConfig pretixApiConfig;
    protected final WebClient webClient;
    protected final PretixContext pretixContext;

    public PretixApiService(PretixApiConfig pretixApiConfig, WebClient webClient, PretixContext pretixContext) {
        this.pretixApiConfig = pretixApiConfig;
        this.webClient = webClient;
        this.pretixContext = pretixContext;
    }

    protected String pretixEvent() {
        return pretixContext.getEvent();
    }
}