/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 
 *
 * @author shihu
 *
 */
public class JdbcTemplateAdapter {

	private final Map<String, JdbcTemplate> JdbcTemplateMap = new HashMap<>();

	public Map<String, JdbcTemplate> getJdbctemplatemap() {
		return JdbcTemplateMap;
	}

	public JdbcTemplate getJdbctemplateByDatasource(String datasource) {
		return JdbcTemplateMap.get(datasource);
	}

	public void putJdbcTemplate(String name, JdbcTemplate jdbc) {
		if ((name != null) && (!name.equals("")) && (jdbc != null)) {
			JdbcTemplateMap.put(name, jdbc);
		}
	}

}
