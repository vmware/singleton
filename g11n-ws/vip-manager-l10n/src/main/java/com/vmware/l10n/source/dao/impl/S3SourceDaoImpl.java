//Copyright 2019-2020 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.source.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.vmware.l10n.record.dao.SqlLiteDao;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.utils.S3Util;
import com.vmware.l10n.utils.SourceUtils;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;

@Repository
public class S3SourceDaoImpl implements SourceDao {

	private static Logger logger = LoggerFactory.getLogger(S3SourceDaoImpl.class);

	@Autowired
	private S3Util s3util;

	@Autowired
	private SqlLiteDao sqlLite;

	/**
	 * the path of local resource file,can be configured in spring config file
	 **/
	@Value("${source.bundle.file.basepath}")
	private String basePath;

	@Override
	public String getFromBundle(SingleComponentDTO componentMessagesDTO) {
		logger.info("Read content from file: {}/{}", componentMessagesDTO.getLocale(),
				componentMessagesDTO.getComponent());

		return s3util.readBundle(basePath, componentMessagesDTO);
	}

	@Override
	public boolean updateToBundle(ComponentMessagesDTO compDTO) {
		logger.info("[Save sources to storage]");

		boolean bExist = false;
		try {
			bExist = s3util.isBundleExist(basePath, compDTO);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}

		if (!s3util.lockBundleFile(basePath, compDTO, 10000)) {
			logger.info("failed to lock bundle file, return.");
			return false;
		}
		
		boolean bSuccess = false;
		try {
			String existingBundle = s3util.readBundle(basePath, compDTO);
			SingleComponentDTO latestDTO = SourceUtils.mergeCacheWithBundle(compDTO, existingBundle);
			bSuccess = s3util.writeBundle(basePath, latestDTO);
			if (bSuccess) {
				if (bExist) {
					logger.info("The bundle file {}/{} is found, update the bundle file.", compDTO.getLocale(),
							compDTO.getComponent());
					sqlLite.updateModifySourceRecord(compDTO);
				} else {
					logger.info("The bundle file {}/{} is not found, cascade create the dir, add new bundle file ",
							compDTO.getLocale(), compDTO.getComponent());
					sqlLite.createSourceRecord(compDTO);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			s3util.unlockBundleFile(basePath, compDTO);
		}

		return bSuccess;
	}
}
