package com.rasa.pilotreceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class CPilotReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(CPilotReceiver.class);

	@Value("${spring.application.name}")
	private String appName;

	@Value("${app.version}")
	private String appVersion;

	public static void main(String[] args) {
		SpringApplication.run(CPilotReceiver.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onReady() {
		LOGGER.info("{} {}: сервер готов к приёму запросов", appName, appVersion);
	}
}
