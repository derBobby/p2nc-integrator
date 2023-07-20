package eu.planlos.pretixtonextcloudintegrator.common.notification;

import eu.planlos.pretixtonextcloudintegrator.common.ApplicationConstants;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class NotificationService implements EnvironmentAware {

    protected List<String> profiles = new ArrayList<>();

    private String prefixTag;

    protected void logNotificationOK() {
        log.debug("Notification has been sent, if enabled.");
    }

    protected void logNotificationError(Exception e) {
        log.error("Notification could not been sent: {}", e.getMessage());
        if(log.isDebugEnabled()) {
            e.printStackTrace();
        }
    }

    protected String prefixSubject(String subject) {
        return String.format("%s - %s", prefixTag, subject);
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {

        profiles = Arrays.stream(environment.getActiveProfiles()).toList();
        String profileString = "UNKNOWN ENVIRONMENT";

        if (profiles.contains(ApplicationConstants.PROFILE_DEV)) {
            profileString = ApplicationConstants.PROFILE_DEV;
        }
        if (profiles.contains(ApplicationConstants.PROFILE_STAGING)) {
            profileString = ApplicationConstants.PROFILE_STAGING;
        }
        if (profiles.contains(ApplicationConstants.PROFILE_PROD)) {
            profileString = ApplicationConstants.PROFILE_PROD;
        }

        prefixTag = String.format("[%s %s]", ApplicationConstants.APP_SHORTNAME, profileString);
        log.debug("Notification tag set to: {}", prefixTag);
    }
}