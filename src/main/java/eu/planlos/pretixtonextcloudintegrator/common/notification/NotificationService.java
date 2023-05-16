package eu.planlos.pretixtonextcloudintegrator.common.notification;

import eu.planlos.pretixtonextcloudintegrator.common.ApplicationProfiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class NotificationService implements EnvironmentAware {

    final protected List<String> profiles = new ArrayList<>();
    protected Environment environment;

    @Value("${spring.application.name:}")
    protected String appName;

    protected String addSubjectPrefix(String subject) {

        String profilePrefix = "UNKNOWN";

        if (profiles.contains(ApplicationProfiles.DEV_PROFILE)) {
            profilePrefix = ApplicationProfiles.DEV_PROFILE;
        }
        if (profiles.contains(ApplicationProfiles.STAGING_PROFILE)) {
            profilePrefix = ApplicationProfiles.STAGING_PROFILE;
        }
        if (profiles.contains(ApplicationProfiles.PROD_PROFILE)) {
            profilePrefix = ApplicationProfiles.PROD_PROFILE;
        }
        return String.format("%s %s - %s", appName, profilePrefix.toUpperCase(), subject);
    }

    protected void logNotificationOK() {
        log.info("Notification has been sent, if enabled.");
    }

    protected void logNotificationError(Exception e) {
        log.error("Notification could not been sent: {}", e.getMessage());
        if(log.isDebugEnabled()) {
            e.printStackTrace();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
