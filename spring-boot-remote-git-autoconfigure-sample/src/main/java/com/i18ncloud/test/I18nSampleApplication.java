package com.i18ncloud.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class I18nSampleApplication{
	private static Logger logger = LoggerFactory.getLogger(I18nSampleApplication.class);
	
	public static void main(String[] args) {
		logger.info("------------------------------begin I18nSampleApplication-------------------------");
		SpringApplication.run(I18nSampleApplication.class, args);
		logger.info("------------------------------end I18nSampleApplication----------------------------");
	}

	
}
