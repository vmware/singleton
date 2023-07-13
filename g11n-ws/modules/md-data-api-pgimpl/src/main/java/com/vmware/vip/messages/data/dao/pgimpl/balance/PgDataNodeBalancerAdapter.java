/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.balance;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.vmware.vip.messages.data.dao.pgimpl.config.JdbcTemplateAdapter;
import com.vmware.vip.messages.data.dao.pgimpl.exception.ProductUnregisteredException;
import com.vmware.vip.messages.data.dao.pgimpl.model.VipProductConfig;
import com.vmware.vip.messages.data.dao.pgimpl.operate.ITabOperate;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */
@Component
public class PgDataNodeBalancerAdapter {
	private static Logger logger = LoggerFactory.getLogger(PgDataNodeBalancerAdapter.class);
	@Autowired
	private JdbcTemplateAdapter dataNodeDBS;
	@Autowired
	private ITabOperate tabOperate;

	private static ConcurrentMap<String, JdbcTemplate> DataNodes = new ConcurrentHashMap<String, JdbcTemplate>();

	@PostConstruct
	public void init() {
		List<VipProductConfig> list = this.tabOperate.getAllProductConfig();
		for (VipProductConfig product : list) {
			logger.info("product:" + product.getProduct() + "------" + product.getDatasource());

			DataNodes.putIfAbsent(product.getProduct(),
					dataNodeDBS.getJdbctemplateByDatasource(product.getDatasource()));
		}
	}

	public JdbcTemplate addProduct2DataNode(String product, JdbcTemplate jdbc) {
		return DataNodes.putIfAbsent(product, jdbc);
	}

	public JdbcTemplate getDataNodeByProduct(String product) throws ProductUnregisteredException {

		JdbcTemplate datanode = DataNodes.get(product);
		if (datanode != null) {
			return datanode;
		} else {
			init();
			datanode = DataNodes.get(product);
			if (datanode == null) {
				throw new ProductUnregisteredException("the product: " + product + " unregistered!");
			}
			return datanode;
		}

	}

	public synchronized void refreshDataNodes() {
		DataNodes = new ConcurrentHashMap<String, JdbcTemplate>();
		init();
	}

	public void removeProduct(String product) {
		DataNodes.remove(product);
	}

}
