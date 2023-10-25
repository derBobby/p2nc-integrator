package eu.planlos.pretixtonextcloudintegrator.nextcloud.service;

import eu.planlos.pretixtonextcloudintegrator.nextcloud.config.NextcloudApiConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public abstract class NextcloudApiService extends ApiService {

    protected final NextcloudApiConfig nextcloudApiConfig;
    protected final WebClient webClient;

    public NextcloudApiService(NextcloudApiConfig nextcloudApiConfig, WebClient webClient) {
        this.nextcloudApiConfig = nextcloudApiConfig;
        this.webClient = webClient;
    }

    @Override
    public boolean isAPIDisabled() {
        if(nextcloudApiConfig.inactive()) {
            log.info("Nextcloud API is not enabled. Returning empty list or null");
            return true;
        }
        return false;
    }
}