/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.operate;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.vmware.vip.messages.data.dao.pgimpl.model.I18nString;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */
public interface IStrOperate {

	public int addStrs(I18nString strs, JdbcTemplate jdbcTemplate);

	public int addStrs(String productName, String version, String component, String locale,
			Map<String, String> messages, JdbcTemplate jdbcTemplate);

	public int addAndUpdateStrs(I18nString strs, JdbcTemplate jdbcTemplate);

	public int addAndUpdateStrs(String productName, String version, String component, String locale,
			Map<String, String> messages, JdbcTemplate jdbcTemplate);

	public Map<String, String> findAllStr(I18nString strs, JdbcTemplate jdbcTemplate);

	public Map<String, String> findAllStr(String productName, String version, String component, String locale,
			Map<String, String> messages, JdbcTemplate jdbcTemplate);

	public int delStrBykey(String productName, String version, String component, String locale, String key,
			JdbcTemplate jdbcTemplate);

	public int delStrBykey(I18nString strs, JdbcTemplate jdbcTemplate);

	public Map<String, String> findByStrKey(I18nString strs, JdbcTemplate jdbcTemplate);

	public Map<String, String> findByStrKey(String productName, String version, String component, String locale,
			String key, JdbcTemplate jdbcTemplate);

	public Map<String, String> findByKeys(String productName, String version, String component, String locale,
			List<String> keys, JdbcTemplate jdbcTemplate);

	public boolean existedComponent(String productName, String version, String component, String locale,
			JdbcTemplate dataNodeByProduct);

}
