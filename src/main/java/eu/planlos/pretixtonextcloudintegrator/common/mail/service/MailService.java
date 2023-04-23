package eu.planlos.pretixtonextcloudintegrator.common.mail.service;

import eu.planlos.pretixtonextcloudintegrator.common.mail.config.MailConfig;
import eu.planlos.pretixtonextcloudintegrator.common.mail.config.MailSender;
import eu.planlos.pretixtonextcloudintegrator.constants.ApplicationProfiles;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class MailService implements EnvironmentAware {

    private static final String MIME_TYPE = "text/html; charset=utf-8";

    final private MailSender mailSender;
    final private MailConfig mailConfig;
    final private List<String> profiles = new ArrayList<>();
    private Environment environment;

    @Value("${spring.application.name:}")
    private String appName;

    public MailService(MailSender mailSender, MailConfig mailConfig) {
        this.mailSender = mailSender;
        this.mailConfig = mailConfig;
    }

    @Async
    public void notifyAdminOfException(String subject, String content, String exceptionMessage) {

        try {
            send(addSubjectPrefix(subject), String.format("%s\n%s", content, exceptionMessage));
            logMailOK();
        } catch (MailException e) {
            logMailError(e);
        }
    }

    @PostConstruct
    @Async
    void notifyStart() {

        profiles.addAll(Arrays.asList(environment.getActiveProfiles()));

        try {
            send(addSubjectPrefix("Application started"), "empty");
            logMailOK();
        } catch (MailException e) {
            logMailError(e);
        }
    }

    private void send(String subject, String content) {

        if(! mailSender.isActive()) {
            log.info("Mail notifications are disabled. Skip sending");
            return;
        }
        log.info("Mail notifications are enabled. Sending");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailConfig.getAdminAddress());
        message.setSubject(subject);
        message.setText(content);
        message.setFrom(mailConfig.getAdminAddress());
        mailSender.send(message);
    }

    private String addSubjectPrefix(String subject) {

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
        return String.format("%s %s - %s", appName, profilePrefix.toLowerCase(), subject);
    }

    private void logMailError(Exception e) {
        log.error("Notification mail could not been sent: {}", e.getMessage());
        if(log.isDebugEnabled()) {
            e.printStackTrace();
        }
    }

    private void logMailOK() {
        log.info("Notification mail has been sent, if enabled.");
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
