/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.api;

import java.nio.channels.ReadableByteChannel;
import java.util.List;

import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultMessageChannel;

public interface IComponentChannelDao {
	public List<ResultMessageChannel> getTransReadableByteChannels(String productName, String version, List<String> components,
			List<String> locales) throws DataException;
	
	public ReadableByteChannel getTransReadableByteChannel(String productName, String version, String component,
			String locale) throws DataException;
	

}
