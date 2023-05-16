package eu.planlos.pretixtonextcloudintegrator.common.notification;

import eu.planlos.pretixtonextcloudintegrator.common.notification.config.MailConfig;
import eu.planlos.pretixtonextcloudintegrator.common.notification.config.MailSender;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class MailService extends NotificationService {

    private static final String MIME_TYPE = "text/html; charset=utf-8";

    final private MailSender mailSender;
    final private MailConfig mailConfig;

    public MailService(MailSender mailSender, MailConfig mailConfig) {
        this.mailSender = mailSender;
        this.mailConfig = mailConfig;
    }

    @PostConstruct
    @Async
    void notifyStart() {

        profiles.addAll(Arrays.asList(environment.getActiveProfiles()));

        try {
            send(addSubjectPrefix("Application started"), "empty");
            logNotificationOK();
        } catch (MailException e) {
            logNotificationError(e);
        }
    }

    @Async
    public void notifyAdmin(String subject, String content) {
        try {
            send(addSubjectPrefix(subject), content);
            logNotificationOK();
        } catch (MailException e) {
            logNotificationError(e);
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
}
