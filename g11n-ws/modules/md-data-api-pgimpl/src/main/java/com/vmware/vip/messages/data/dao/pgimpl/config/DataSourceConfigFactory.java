/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 
 *
 * @author shihu
 *
 */
@Configuration
public class DataSourceConfigFactory {

	private static Logger logger = LogManager.getLogger(DataSourceConfigFactory.class);
	@Autowired
	private DataSourceProperties dsProps;

	@Bean
	public JdbcTemplateAdapter dataSource2JdbcTemplate() {
		logger.info("-----------------begin datanode databasconfig----------------------------- ");
		Map<String, DataSource> druidDataSource = new HashMap<>();
		for (Map<String, String> map : dsProps.getDatasoures()) {
			Properties props = new Properties();

			for (Entry<String, String> entry : map.entrySet()) {

				logger.debug(entry.getKey() + "---------" + entry.getValue());

				props.setProperty("druid." + entry.getKey(), entry.getValue());
			}

			DruidDataSource subdds = new DruidDataSource();
			subdds.configFromPropety(props);

			logger.info("init datasource" + subdds.getName());
			druidDataSource.put(subdds.getName(), subdds);
			logger.info("-----------------end dataNode database config----------------------------- ");
		}

		
		if(druidDataSource.size()<1) {
			logger.fatal("there no data DB resource of VIP");
		}
		
		
		JdbcTemplateAdapter jdbcTemplateAdapter = new JdbcTemplateAdapter();

		for (Entry<String, DataSource> dsEntry : druidDataSource.entrySet()) {
			jdbcTemplateAdapter.putJdbcTemplate(dsEntry.getKey(), new JdbcTemplate(dsEntry.getValue()));
		}

		return jdbcTemplateAdapter;
	}

}
