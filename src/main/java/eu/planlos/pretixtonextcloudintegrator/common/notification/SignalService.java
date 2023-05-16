package eu.planlos.pretixtonextcloudintegrator.common.notification;

import eu.planlos.pretixtonextcloudintegrator.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.common.notification.config.SignalApiConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
@Service
public class SignalService extends NotificationService {

    private final SignalApiConfig config;
    private final WebClient webClient;

    public static final String SIGNAL_JSON = "{\"message\": \"%s\", \"number\": \"%s\", \"recipients\": [ \"%s\" ]}";

    public SignalService(SignalApiConfig config, @Qualifier("SignalWebClient") WebClient webClient) {
        this.config = config;
        this.webClient = webClient;
    }

    @PostConstruct
    @Async
    void notifyStart() {

        profiles.addAll(Arrays.asList(environment.getActiveProfiles()));

        try {
            send("Application started");
            logNotificationOK();
        } catch (MailException e) {
            logNotificationError(e);
        }
    }

    @Async
    public void notifyAdmin(String message) {
        try {
            send(message);
            logNotificationOK();
        } catch (MailException e) {
            logNotificationError(e);
        }
    }

    private void send(String message) {

        if(! config.active()) {
            log.info("Signal notifications are disabled. Skip sending");
            return;
        }
        log.info("Signal notifications are enabled. Sending");

        String prefixedMessage = addSubjectPrefix(message);

        try {
            String apiResponse = webClient
                    .post()
                    .uri("/v2/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonMessage(prefixedMessage))
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                    .doOnError(error -> log.error("Notification failed."))
                    .block();
            if (apiResponse != null) {
                log.info("Response is: {}", apiResponse);
                return;
            }

            throw new ApiException("ApiResponse object is NULL");
        } catch (WebClientResponseException e) {
            throw new ApiException(e);
        }
    }

    private Object jsonMessage(String message) {
        return String.format(SIGNAL_JSON, message, config.phoneSender(), config.phoneReceiver());
    }
}

