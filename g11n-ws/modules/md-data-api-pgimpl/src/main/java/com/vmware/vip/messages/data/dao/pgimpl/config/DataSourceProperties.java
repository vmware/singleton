/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource.postgres")
public class DataSourceProperties {
	private List<Map<String, String>> datasoures = new ArrayList<Map<String, String>>();

	public List<Map<String, String>> getDatasoures() {
		return datasoures;
	}

	public void setDatasoures(List<Map<String, String>> datasoures) {
		this.datasoures = datasoures;
	}
}
