package com.app.pedido.service;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class SmtpEmailService extends AEmailService {

	private static final Logger logger = LoggerFactory.getLogger(SmtpEmailService.class);

	@Autowired
	private MailSender mailSender;

	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public void sendEmail(SimpleMailMessage msg) {
		logger.info(" envio de Email...");
		mailSender.send(msg);
		logger.info("Email enviado!");

	}

	@Override
	public void sendHtmlEmail(MimeMessage msg) {
		logger.info(" envio de Email...");
		javaMailSender.send(msg);
		logger.info("Email enviado!");

	}

}
