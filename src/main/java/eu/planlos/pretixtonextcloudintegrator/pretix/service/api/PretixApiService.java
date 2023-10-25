package eu.planlos.pretixtonextcloudintegrator.pretix.service.api;

import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public abstract class PretixApiService extends ApiService {

    protected final PretixApiConfig pretixApiConfig;
    protected final WebClient webClient;

    public PretixApiService(PretixApiConfig pretixApiConfig, WebClient webClient) {
        this.pretixApiConfig = pretixApiConfig;
        this.webClient = webClient;
    }

    @Override
    public boolean isAPIDisabled() {
        if(pretixApiConfig.inactive()) {
            log.info("Pretix API is not enabled. Returning empty list or null");
            return true;
        }
        return false;
    }
}