package eu.planlos.p2ncintegrator.pretix.service.api;

import eu.planlos.p2ncintegrator.pretix.config.PretixApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public abstract class PretixApiService extends ApiService {

    protected final PretixApiConfig config;
    protected final WebClient webClient;

    public PretixApiService(PretixApiConfig config, WebClient webClient) {
        this.config = config;
        this.webClient = webClient;
    }

    @Override
    public boolean isAPIDisabled() {
        if(config.inactive()) {
            log.info("Pretix API is not enabled. Returning empty list or null");
            return true;
        }
        return false;
    }
}