package eu.planlos.p2ncintegrator.common.notification.config;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class MailSender extends JavaMailSenderImpl {
	
	final private boolean active;
		
	/**
	 * By default sending of mails is inactive
	 */
	public MailSender(boolean active) {
		this.active = active;
	}

	public MailSender(boolean active, String mailHost, int mailPort, String mailUsername, String mailPassword, Properties props) {
		this.active = active;
		setHost(mailHost);
		setPort(mailPort);
		setUsername(mailUsername);
		setPassword(mailPassword);
		setJavaMailProperties(props);
	}

	public boolean isActive() {
		return this.active;
	}
}
