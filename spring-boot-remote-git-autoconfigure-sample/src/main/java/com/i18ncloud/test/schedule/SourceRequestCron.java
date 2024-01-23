package com.i18ncloud.test.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


/**
 * 
 *
 * @author shihu
 *
 */
/**
 * This implementation of interface SourceService.
 */
@Service
public class SourceRequestCron {
	private static Logger logger = LoggerFactory.getLogger(SourceRequestCron.class);


	private String testVal="test";

	@Scheduled(fixedDelay = 1000 * 10)
	public void syncToInternali18nManager() {
		logger.info(testVal);



	}


}
