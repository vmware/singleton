//Copyright 2019-2020 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.source.dao.impl;

import java.io.File;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.l10n.record.dao.SqlLiteDao;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.utils.S3Util;
import com.vmware.l10n.utils.S3Util.Locker;
import com.vmware.l10n.utils.SourceUtils;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;

@Repository
@Profile("s3")
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

	@PostConstruct
	private void init() {
		if (basePath.startsWith("/")) {
			basePath = basePath.substring(1);
		}
		if (!basePath.isEmpty() && !basePath.endsWith(ConstantsChar.BACKSLASH)) {
			basePath += ConstantsChar.BACKSLASH;
		}
		basePath += ConstantsFile.L10N_BUNDLES_PATH;
		basePath = basePath.replace(File.separator, ConstantsChar.BACKSLASH);
	}

	@Override
	public String getFromBundle(SingleComponentDTO componentMessagesDTO) {
		logger.debug("Read content from file: {}/{}", componentMessagesDTO.getLocale(),
				componentMessagesDTO.getComponent());

		return s3util.readBundle(basePath, componentMessagesDTO);
	}

	@Override
	public boolean updateToBundle(ComponentMessagesDTO compDTO) throws JsonProcessingException {
		logger.debug("[Save sources to storage]: {}/{}/{}/{}", compDTO.getProductName(), compDTO.getVersion(),
				compDTO.getComponent(), compDTO.getLocale());

		Locker locker = s3util.new Locker(basePath, compDTO);
		if (!locker.lockFile()) {
			logger.warn("failed to lock bundle file, return.");
			return false;
		}

		boolean bExist = s3util.isBundleExist(basePath, compDTO);

		boolean bSuccess = false;
		try {
			if (bExist) {
				String existingBundle = s3util.readBundle(basePath, compDTO);
				SingleComponentDTO latestDTO = SourceUtils.mergeCacheWithBundle(compDTO, existingBundle);
				bSuccess = s3util.writeBundle(basePath, latestDTO);
			} else {
				bSuccess = s3util.writeBundle(basePath, compDTO);
			}

			if (bSuccess) {
				if (bExist) {
					logger.debug("The bundle file {}/{} is found, update the bundle file.", compDTO.getLocale(),
							compDTO.getComponent());
					sqlLite.updateModifySourceRecord(compDTO);
				} else {
					logger.debug("The bundle file {}/{} is not found, cascade create the dir, add new bundle file ",
							compDTO.getLocale(), compDTO.getComponent());
					sqlLite.createSourceRecord(compDTO);
				}
			}
		} finally {
			locker.unlockFile();
		}

		return bSuccess;
	}
}
