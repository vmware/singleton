/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.impl;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.messages.data.bundle.BundleConfig;
import com.vmware.vip.messages.data.dao.api.IComponentChannelDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultMessageChannel;
import com.vmware.vip.messages.data.exception.BundleException;
@Profile("bundle")
@Repository
public class FileComponentChannelDao implements IComponentChannelDao{
	private static Logger logger = LoggerFactory.getLogger(FileComponentChannelDao.class);
	@Autowired
	private BundleConfig bundleConfig;
	
	@Override
	public List<ResultMessageChannel> getTransReadableByteChannels(String productName, String version,
			List<String> components, List<String> locales) throws DataException {
		
		List<ResultMessageChannel> resultChannels = new ArrayList<ResultMessageChannel>();
		for (String component : components) {
			for (String locale : locales) {
				String subpath = ConstantsFile.L10N_BUNDLES_PATH + productName
					     + File.separator + version + File.separator + component + File.separator
						+ ResourceFilePathGetter.getLocalizedJSONFileName(locale);
				String jsonfile = bundleConfig.getBasePathWithSeparator() + subpath;
				File file = new File(jsonfile);
				if (file.exists()) {
					try {
						resultChannels.add(new ResultMessageChannel(component, locale, FileChannel.open(file.toPath(), StandardOpenOption.READ)));
					} catch (IOException e) {
						throw new BundleException(e.getMessage(), e);
					}
				}
			}
		}
		logger.debug("fileSize: {}", resultChannels.size());
		
		return resultChannels;
	}

}
