/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class L10nAgentApplication{
	private static Logger logger = LoggerFactory.getLogger(L10nAgentApplication.class);
	
	public static void main(String[] args) {
		logger.info("------------------------------begin L10nAgentApplication-------------------------");
		SpringApplication.run(L10nAgentApplication.class, args);
		logger.info("------------------------------end L10nAgentApplication----------------------------");
	}

	
}
