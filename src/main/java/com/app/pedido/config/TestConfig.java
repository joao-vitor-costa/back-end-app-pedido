package com.app.pedido.config;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.app.pedido.service.DBService;
import com.app.pedido.service.IEmailService;
import com.app.pedido.service.MockEmailService;

@Configuration
@Profile("test")
public class TestConfig {

	@Autowired
	private DBService dbService;

	@Bean
	public boolean instantiateDataBse() throws ParseException {
		dbService.instantiateTestDataBase();
		return true;
	}

	@Bean
	public IEmailService emailService() {
		return new MockEmailService();
	}

}
