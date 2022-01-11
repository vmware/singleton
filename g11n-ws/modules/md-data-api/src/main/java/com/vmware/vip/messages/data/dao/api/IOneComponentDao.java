/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.api;

import java.util.Map;

import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultI18Message;

/**
 * 
 *
 * @author shihu
 *
 */
public interface IOneComponentDao {

	public ResultI18Message get(String productName, String version, String component, String locale)
			throws DataException;

	public String get2JsonStr(String productName, String version, String component, String locale) throws DataException;

	public boolean add(String productName, String version, String component, String locale,
			Map<String, String> messages) throws DataException;

	public boolean update(String productName, String version, String component, String locale,
			Map<String, String> messages) throws DataException;

	public boolean delete(String productName, String version, String component, String locale) throws DataException;
}
