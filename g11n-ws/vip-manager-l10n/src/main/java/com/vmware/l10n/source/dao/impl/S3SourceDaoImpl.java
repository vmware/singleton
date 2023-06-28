//Copyright 2019-2022 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.source.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.l10n.conf.S3Cfg;
import com.vmware.l10n.conf.S3Client;
import com.vmware.l10n.record.model.RecordModel;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.utils.S3Util;
import com.vmware.l10n.utils.S3Util.Locker;
import com.vmware.l10n.utils.SourceUtils;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;

@Repository
@Profile("s3")
public class S3SourceDaoImpl implements SourceDao {

	private static Logger logger = LoggerFactory.getLogger(S3SourceDaoImpl.class);
    @Autowired
    private S3Client s3Client;
    @Autowired
    private S3Cfg config;
	@Autowired
	private S3Util s3util;


	/**
	 * the path of local resource file,can be configured in spring config file
	 **/
	@Value("${source.bundle.file.basepath}")
	private String basePath;

	@PostConstruct
	private void init() {
		if (basePath.startsWith("/")) {
			basePath = basePath.substring(1);
		}else if(basePath.startsWith(ConstantsChar.DOT+ConstantsChar.BACKSLASH)) {
			basePath = basePath.replace(ConstantsChar.DOT, "").replace(ConstantsChar.BACKSLASH, "");
		}
		if (!basePath.isEmpty() && !basePath.endsWith(ConstantsChar.BACKSLASH)) {
			basePath += ConstantsChar.BACKSLASH;
		}
		basePath += ConstantsFile.L10N_BUNDLES_PATH;
		basePath = basePath.replace("\\", ConstantsChar.BACKSLASH);
	}

	@Override
	public String getFromBundle(SingleComponentDTO componentMessagesDTO) {
		logger.debug("Read content from file: {}/{}", componentMessagesDTO.getLocale(),
				componentMessagesDTO.getComponent());

		return s3util.readBundle(this.basePath, componentMessagesDTO);
	}

	@Override
	public boolean updateToBundle(ComponentMessagesDTO compDTO) throws JsonProcessingException {
		logger.debug("[Save sources to storage]: {}/{}/{}/{}", compDTO.getProductName(), compDTO.getVersion(),
				compDTO.getComponent(), compDTO.getLocale());

		Locker locker = s3util.new Locker(this.basePath, compDTO);
		if (!locker.lockFile()) {
			logger.warn("failed to lock bundle file, return.");
			return false;
		}

		boolean bSuccess = false;
		try {
			boolean bExist = s3util.isBundleExist(this.basePath, compDTO);
			if (bExist) {
				String existingBundle = s3util.readBundle(this.basePath, compDTO);
				SingleComponentDTO latestDTO = SourceUtils.mergeCacheWithBundle(compDTO, existingBundle);
        bSuccess = s3util.writeBundle(this.basePath, latestDTO);
			} else {
        bSuccess = s3util.writeBundle(this.basePath, compDTO);
			}
			if (bSuccess) {
				if (bExist) {
					logger.debug("The bundle file {}/{} is found, update the bundle file.", compDTO.getLocale(),
							compDTO.getComponent());

				} else {
					logger.debug("The bundle file {}/{} is not found, cascade create the dir, add new bundle file ",
							compDTO.getLocale(), compDTO.getComponent());

				}
			}
		} finally {
			locker.unlockFile();
		}

		return bSuccess;
	}
	
	@Override
	public List<RecordModel> getUpdateRecords(String productName, String version, long lastModifyTime) throws L10nAPIException{
		
		List<RecordModel> records = new ArrayList<RecordModel>();
		ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(config.getBucketName());
		
	    String latestJsonFile = ConstantsFile.LOCAL_FILE_SUFFIX+ConstantsChar.UNDERLINE+ConstantsKeys.LATEST+ConstantsFile.FILE_TPYE_JSON;
		StringBuilder prefix = new StringBuilder();
		prefix.append(this.basePath);
		if (!StringUtils.isEmpty(productName)) {
			prefix.append(productName);
			prefix.append(ConstantsChar.BACKSLASH);
		}
		if (!StringUtils.isEmpty(version)) {
			prefix.append(version);
			prefix.append(ConstantsChar.BACKSLASH);
		}
	    logger.info("begin getUpdateRecords lastModyTime: {}, prefix: {}", lastModifyTime, prefix.toString());
	    req.setPrefix(prefix.toString());
	    
        ListObjectsV2Result result;
        do {
            result = s3Client.getS3Client().listObjectsV2(req);
            for (S3ObjectSummary oSy : result.getObjectSummaries()) {
          	  String keyStr = oSy.getKey();
          	  long  currentModifyTime = oSy.getLastModified().getTime();
          	  if(keyStr.endsWith(latestJsonFile)
          			  && currentModifyTime>lastModifyTime) {
          		logger.info("Need Update:{}:{}", keyStr, currentModifyTime);
          		records.add(SourceUtils.parseKeyStr2Record(keyStr,this.basePath, currentModifyTime));
          	  }

            }
            String token = result.getNextContinuationToken();
            req.setContinuationToken(token);
        } while (result.isTruncated());
        return records;
	}

	
}
