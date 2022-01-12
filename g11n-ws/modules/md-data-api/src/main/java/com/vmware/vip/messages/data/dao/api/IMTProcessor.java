/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.api;

import java.util.List;

import com.vmware.vip.messages.data.dao.exception.MTException;

/**
 * 
 *
 * @author shihu
 *
 */
public interface IMTProcessor {

	public String translateString(String fromLang, String toLang, String source) throws MTException;

	public List<String> translateArray(String fromLang, String toLang, List<String> sourceList) throws MTException;
}
