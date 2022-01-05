/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.synch.dao;

import java.io.File;
import java.util.TreeMap;

import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.synch.model.SyncI18nMsg;

public interface SynchComponentDao {

	public SyncI18nMsg get(String productName, String version, String component, String locale)throws DataException;
	public File update(String productName, String version, String component, String locale, TreeMap<String, String> map) throws DataException;
	// TODO
	public boolean delete(String productName, String version, String component, String locale) ;
}
