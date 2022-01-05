/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.api;

import java.util.List;

import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultI18Message;

/**
 * 
 *
 * @author shihu
 *
 */
public interface IMultComponentDao {
	public List<String> get2JsonStrs(String productName, String version, List<String> components, List<String> locales)
			throws DataException;

	public List<ResultI18Message> get(String productName, String version, List<String> components, List<String> locales)
			throws DataException;

}
