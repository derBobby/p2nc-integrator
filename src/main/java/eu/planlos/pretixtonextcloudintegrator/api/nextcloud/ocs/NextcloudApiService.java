package eu.planlos.pretixtonextcloudintegrator.api.nextcloud.ocs;

import eu.planlos.pretixtonextcloudintegrator.api.nextcloud.config.NextcloudApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public abstract class NextcloudApiService {

    protected final NextcloudApiConfig nextcloudApiConfig;
    protected final WebClient webClient;

    public NextcloudApiService(NextcloudApiConfig nextcloudApiConfig, WebClient webClient) {
        this.nextcloudApiConfig = nextcloudApiConfig;
        this.webClient = webClient;
    }
}