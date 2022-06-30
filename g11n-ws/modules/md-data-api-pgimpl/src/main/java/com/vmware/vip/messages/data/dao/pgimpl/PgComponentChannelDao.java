/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl;

import java.io.ByteArrayInputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vmware.vip.messages.data.dao.api.IComponentChannelDao;
import com.vmware.vip.messages.data.dao.exception.DataException;

@Repository
public class PgComponentChannelDao implements IComponentChannelDao {
	private static Logger logger = LoggerFactory.getLogger(PgComponentChannelDao.class);

	@Autowired
	private PgMultCompApiImpl pgMultComp;

	@Autowired
	private PgOneComponentApiImpl pgOneComponentApiImpl;

	@Override
	public List<ReadableByteChannel> getTransReadableByteChannels(String productName, String version,
			List<String> components, List<String> locales) throws DataException {
		List<String> results = pgMultComp.get2JsonStrs(productName, version, components, locales);
		List<ReadableByteChannel> list = new ArrayList<>();
		for (String resultStr : results) {
			ByteArrayInputStream stringInputStream = new ByteArrayInputStream(resultStr.getBytes());
			list.add(Channels.newChannel(stringInputStream));
		}

		logger.info("Message Size: {}", list.size());
		return list;
	}

	@Override
	public ReadableByteChannel getTransReadableByteChannel(String productName, String version, String component,
			String locale) throws DataException {
		String resultStr = pgOneComponentApiImpl.get2JsonStr(productName, version, component, locale);
		ByteArrayInputStream stringInputStream = new ByteArrayInputStream(resultStr.getBytes());
		return Channels.newChannel(stringInputStream);
	}

}
