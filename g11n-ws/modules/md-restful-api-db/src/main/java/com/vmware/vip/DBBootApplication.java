/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 *
 * @author shihu
 *
 */
@SpringBootApplication
@EnableSwagger2
public class DBBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(DBBootApplication.class, args);
	}
}
