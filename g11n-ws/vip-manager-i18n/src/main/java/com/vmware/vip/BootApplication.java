/*
 * Copyright 2019-2021 VMware, Inc.
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
public class BootApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(BootApplication.class);
	}

	public static void main(String[] args) throws Exception {
		System.setProperty(
				"org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH",
				"true");
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
		SpringApplication.run(BootApplication.class, args);
	}
	
	


}
