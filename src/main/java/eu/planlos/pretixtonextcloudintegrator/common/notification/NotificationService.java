package eu.planlos.pretixtonextcloudintegrator.common.notification;

import eu.planlos.pretixtonextcloudintegrator.common.ApplicationConstants;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class NotificationService implements EnvironmentAware {

    final protected List<String> profiles = new ArrayList<>();
    protected Environment environment;

    private String prefixTag;

    protected void logNotificationOK() {
        log.info("Notification has been sent, if enabled.");
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
        this.environment = environment;
    }

    @PostConstruct
    public void preparePrefixTag() {

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
    }
}
