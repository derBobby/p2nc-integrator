package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public abstract class PretixApiService {

    protected final PretixApiConfig pretixApiConfig;
    protected final WebClient webClient;

    public PretixApiService(PretixApiConfig pretixApiConfig, WebClient webClient) {
        this.pretixApiConfig = pretixApiConfig;
        this.webClient = webClient;
    }
}