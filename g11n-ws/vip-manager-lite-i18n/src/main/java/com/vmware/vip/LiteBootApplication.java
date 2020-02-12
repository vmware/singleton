/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = { "com.vmware" })
public class LiteBootApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(LiteBootApplication.class);
	}

	public static void main(String[] args) throws Exception {

		if (args.length > 0) {
			for (String arg : args) {
				
				if (arg.equalsIgnoreCase("-c")|| arg.equalsIgnoreCase("-clean")) {
					
					
					System.setProperty("translation.bundle.file.clean", "true");
					
					break;
				}
			}
		} else {
			System.setProperty("translation.bundle.file.clean", "false");
		}
		SpringApplication.run(LiteBootApplication.class, args);
	}
	
	


}