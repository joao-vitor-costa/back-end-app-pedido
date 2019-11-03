package com.app.pedido.service;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

public class MockEmailService extends AEmailService {

	private static final Logger logger = LoggerFactory.getLogger(MockEmailService.class);

	@Override
	public void sendEmail(SimpleMailMessage msg) {
		logger.info("Simulando envio de Email...");
		logger.info(msg.toString());
		logger.info("Email enviado!");
	}

	@Override
	public void sendHtmlEmail(MimeMessage msg) {
		logger.info("Simulando envio de Email HTML...");
		logger.info(msg.toString());
		logger.info("Email enviado!");

	}

}
