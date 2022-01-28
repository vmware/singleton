/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.operate;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.vmware.vip.messages.data.dao.pgimpl.model.VipProductConfig;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */
public interface ITabOperate {
	public List<VipProductConfig> getAllProductConfig();

	public Map<String, Integer> aggrByDataSource(Map<String, Integer> map);

	public int addProduct(VipProductConfig config);

	public boolean createProductTab(String product, JdbcTemplate jdbcTemplate);

	public boolean isExistedProduct(String productName);

	public int delProduct(String product);

	public void delProductTab(String product, JdbcTemplate jdbcTemplate);
	
	public int clearProductData(String productName, String version, JdbcTemplate jdbcTemplate);

}
