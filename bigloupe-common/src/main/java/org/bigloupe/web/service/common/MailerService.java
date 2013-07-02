package org.bigloupe.web.service.common;

import java.io.File;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service to send email
 * 
 * @author bigloupe
 * 
 */
@Service
public class MailerService {

	private Log log = LogFactory.getLog(MailerService.class);

	@Autowired
	JavaMailSenderImpl mailSender;

	/**
	 * Send email in asynchronous way
	 * 
	 * @param username
	 * @param email
	 * @param request
	 * @param requestUrl
	 */
	@Async
	public void sendEmailIfPossible(String senderAddress,
			List<String> emailList, String subject, String body) {
		// Send an email to this new user
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			mimeMessage.setHeader("Content-Type", "text/html; charset=utf-8");
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,
					"UTF-8");
			helper.setText(
					"<html><body><p>" + body + "</body></html>", true);
			for (String email : emailList) {
				helper.setTo(email);
				helper.setFrom(new InternetAddress(mailSender.getUsername(),
						senderAddress));
				helper.setSubject(subject);
				mailSender.send(mimeMessage);
			}
		} catch (Exception ex) {
			log.debug(ex.getMessage());
			ex.printStackTrace();
		}

	}

	
}
