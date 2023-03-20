/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.operate;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.vmware.vip.messages.data.dao.pgimpl.model.I18nDocument;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */
public interface IDocOperate {

	public I18nDocument findByDocId(I18nDocument doc, JdbcTemplate jdbcTemplate);

	public int saveDoc(I18nDocument content, JdbcTemplate jdbcTemplate);

	public int removeDoc(I18nDocument dco, JdbcTemplate jdbcTemplate);

	public String findByDocId(String productName, String version, String component, String locale,
			JdbcTemplate jdbcTemplate);

	public int removeDoc(String productName, String version, String component, String locale,
			JdbcTemplate jdbcTemplate);

	public int saveDoc(String productName, String version, String component, String locale,
			Map<String, String> messages, JdbcTemplate jdbcTemplate);

	public int updateDoc(String productName, String version, String component, String locale,
			Map<String, String> messages, JdbcTemplate jdbcTemplate);

	public List<String> getComponentList(String productName, String version, JdbcTemplate jdbcTemplate);

	public List<String> getLocaleList(String productName, String version, JdbcTemplate jdbcTemplate);
	/**
	 * get a product's all available version list
	 * @param productName
     * @param jdbcTemplate
	  * @return
	 */
	public List<String> getVersionList(String productName, JdbcTemplate jdbcTemplate);

}
