package eu.planlos.pretixtonextcloudintegrator.common.notification.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class MailConfig {

	@Value("${eu.planlos.pretixtonextcloudintegrator.mail.active:false}")
	private boolean active;

	@Getter
	@Value("${eu.planlos.pretixtonextcloudintegrator.mail.adminadress:@null}")
	private String adminAddress;

	@Value("${spring.mail.properties.mail.transport.protocol:@null}")
	private String mailTransportProtocol;

	@Value("${spring.mail.properties.mail.smtp.auth:@null}")
	private String mailSmtpAuth;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable:@null}")
	private String mailSmtpStarttlsEnable;

	@Value("${spring.mail.properties.mail.debug:@null}")
	private String mailDebug;

	@Value("${spring.mail.host:#null}")
	private String mailHost;

	@Value("${spring.mail.port:0}")
	private int mailPort;

	@Value("${spring.mail.username:#null}")
	private String mailUsername;

	@Value("${spring.mail.password:#null}")
	private String mailPassword;

	@Bean
	public MailSender mailSender() {

		// Initialize mail sender, if mail sending is active
		if(active) {
			Properties props = new Properties();
			props.put("mail.transport.protocol", mailTransportProtocol);
			props.put("mail.smtp.auth", mailSmtpAuth);
			props.put("mail.smtp.starttls.enable", mailSmtpStarttlsEnable);
			props.put("mail.debug", mailDebug);
			return new MailSender(true, mailHost, mailPort, mailUsername, mailPassword, props);
		}

		// Else inactive mail sender
		return new MailSender(false);
	}
}