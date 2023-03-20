//Copyright 2019-2022 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.utils;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.l10n.conf.S3Cfg;
import com.vmware.l10n.conf.S3Client;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.common.utils.TimeUtils;

@Component("S3Util")
@Profile("s3")
public class S3Util {
	private static Logger logger = LoggerFactory.getLogger(S3Util.class);

	private static Random random = new Random(System.currentTimeMillis());
	private static long retryInterval = 500; // milliseconds
	private static long deadlockInterval = 30 * 1000L;
	private static long waitToLock = 10 * 1000L; // 10 seconds

	@Autowired
	private S3Cfg config;

	@Autowired
	private S3Client s3Client;

	public String readBundle(String basePath, SingleComponentDTO compDTO) {
		String bundlePath = getBundleFilePath(basePath, compDTO);
		return s3Client.readObject(bundlePath);
	}

	public boolean writeBundle(String basePath, SingleComponentDTO compDTO) throws JsonProcessingException {
		String bundlePath = getBundleFilePath(basePath, compDTO);
		s3Client.putObject(bundlePath, compDTO.toPrettyString());
		return true;
	}

	public boolean isBundleExist(String basePath, SingleComponentDTO singleComponentDTO) {
		String bundlePath = getBundleFilePath(basePath, singleComponentDTO);
		return s3Client.isObjectExist(bundlePath);
	}

	public static String getBundleFilePath(String basePath, SingleComponentDTO dto) {
		if (StringUtils.isEmpty(dto.getComponent())) {
			dto.setComponent(ConstantsFile.DEFAULT_COMPONENT);
		}
		return genProductVersionS3Path(basePath, dto.getProductName(), dto.getVersion()) + dto.getComponent()
		+ ConstantsChar.BACKSLASH + ResourceFilePathGetter.getLocalizedJSONFileName(dto.getLocale());
	}

	/**
	 * generate the product version path
	 */
	public static String genProductVersionS3Path(String basePath, String productName, String version) {
		StringBuilder path = new StringBuilder();
		path.append(basePath);
		path.append(productName);
		path.append(ConstantsChar.BACKSLASH);
		path.append(version);
		path.append(ConstantsChar.BACKSLASH);
		return path.toString();
	}

	public class Locker {
		private final String key;

		public Locker(String basePath, SingleComponentDTO compDTO) {
			String bundlePath = getBundleFilePath(basePath, compDTO);
			this.key = bundlePath.substring(0, bundlePath.lastIndexOf('.')) + ".lock";
		}

		public boolean lockFile() {
			long endTime = System.currentTimeMillis() + waitToLock;
			String content = Double.toString(random.nextDouble());

			do {
				if (!waitLockfileDisappeared(endTime)) {
					return false;
				}

				// Write the lock file
				ListVersionsRequest lvr = new ListVersionsRequest();
				lvr.setBucketName(config.getBucketName());
				lvr.setPrefix(this.key);
				lvr.setMaxResults(2);
				PutObjectResult pObjResult = s3Client.putObject(this.key, content);
				// Check file content is correct
				String putVersionId = pObjResult.getVersionId();
				try {
			    
				    VersionListing versionListing = s3Client.getS3Client().listVersions(lvr);
				    String getVersionId = versionListing.getVersionSummaries().get(0).getVersionId();
				    int size = versionListing.getVersionSummaries().size();
				   
				    if (size == 1 && putVersionId.equals(getVersionId)) {
						return true;
					}
					logger.warn("putVersiongId:{}, getVersionId：{}", putVersionId, getVersionId);
				} catch (AmazonS3Exception e) {
					logger.error(e.getMessage(), e);
				}
				TimeUtils.sleep(retryInterval);
			} while (System.currentTimeMillis() < endTime);

			return false;
		}

		public void unlockFile() {
			try {
				ListVersionsRequest listVersionsRequest = new ListVersionsRequest();
		    	listVersionsRequest.setBucketName(config.getBucketName());
		    	listVersionsRequest.setPrefix(this.key);
		        VersionListing versionListing = s3Client.getS3Client().listVersions(listVersionsRequest);
		        while (true) 
		        {
		            for (S3VersionSummary vs : versionListing.getVersionSummaries()) {
		            	logger.debug("-------------------delete versionId------------------{}", vs.getVersionId());
		            	s3Client.getS3Client().deleteVersion(config.getBucketName(), vs.getKey(), vs.getVersionId());
		            }
		            
		            if (versionListing.isTruncated()) {
		                versionListing = s3Client.getS3Client().listNextBatchOfVersions(versionListing);
		            } else {
		                break;
		            }
		        }
			} catch (SdkClientException e) {
				logger.error(e.getMessage(), e);
			}
		}

		private boolean waitLockfileDisappeared(long endTime) {
			boolean bDeadlockTested = false;
			boolean existed = s3Client.getS3Client().doesObjectExist(config.getBucketName(), this.key);
			while (existed) {
				try {
				if (!bDeadlockTested) {
					bDeadlockTested = true;
					// Get file creation time to detect deadlock
					ObjectMetadata objectMeta = s3Client.getS3Client().getObjectMetadata(config.getBucketName(), this.key);
					Date lastModified = objectMeta.getLastModified();
					if (new Date().getTime() - lastModified.getTime() > deadlockInterval) {// longer than 10min
						unlockFile();
						logger.warn("deleted dead lock file");
						return true;
					}
				}
				}catch(Exception e) {
					logger.warn(e.getMessage(), e);
				}
				TimeUtils.sleep(retryInterval);
				if (System.currentTimeMillis() >= endTime) {
					logger.warn("failed to wait for lockfile disappeared");
					return false;
				}

				existed =  s3Client.getS3Client().doesObjectExist(config.getBucketName(), this.key);
			}

			return true;
		}
	}
	
}