//Copyright 2019-2020 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.utils;

import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3ObjectSummary;
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
	private static long deadlockInterval = 10 * 60 * 1000L; // 10 minutes
	private static long waitS3Operation = 100; // milliseconds
	private static long waitToLock = 10 * 1000L; // 10 seconds

	private AmazonS3 s3Inst;

	@Autowired
	private S3Client client;

	/**
	 * the s3 bucket Name
	 */
	@Value("${s3.bucketName}")
	private String bucketName;

	@PostConstruct
	private void init() {
		s3Inst = client.getS3Client();
	}

	public String readBundle(String basePath, SingleComponentDTO compDTO) {
		String bundlePath = getBundleFilePath(basePath, compDTO);
		return s3Inst.getObjectAsString(bucketName, bundlePath);
	}

	public boolean writeBundle(String basePath, SingleComponentDTO compDTO) {
		try {
			String bundlePath = getBundleFilePath(basePath, compDTO);
			s3Inst.putObject(bucketName, bundlePath, compDTO.toPrettyString());
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	public boolean isBundleExist(String basePath, SingleComponentDTO singleComponentDTO) {
		String bundlePath = getBundleFilePath(basePath, singleComponentDTO);
		return s3Inst.doesObjectExist(bucketName, bundlePath);
	}

	private static String getBundleFilePath(String basePath, SingleComponentDTO dto) {
		if (StringUtils.isEmpty(dto.getComponent())) {
			dto.setComponent(ConstantsFile.DEFAULT_COMPONENT);
		}
		return genProductVersionS3Path(basePath, dto.getProductName(), dto.getVersion()) + dto.getComponent()
				+ ConstantsChar.BACKSLASH + ResourceFilePathGetter.getLocalizedJSONFileName(dto.getLocale());
	}

	/**
	 * generate the product version path
	 */
	private static String genProductVersionS3Path(String basePath, String productName, String version) {
		StringBuilder path = new StringBuilder();
		path.append(basePath);
		if (!basePath.endsWith(ConstantsChar.BACKSLASH)) {
			path.append(ConstantsChar.BACKSLASH);
		}
		path.append(productName);
		path.append(ConstantsChar.BACKSLASH);
		path.append(version);
		path.append(ConstantsChar.BACKSLASH);
		return path.toString();

	}

	public class Locker {
		private String key;

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
				s3Inst.putObject(bucketName, this.key, content);
				TimeUtils.sleep(waitS3Operation); // Wait for a while to let S3 finish writing.

				// Check file content is correct
				try {
					if (content.equals(s3Inst.getObjectAsString(bucketName, this.key))) {
						return true;
					}
				} catch (AmazonS3Exception e) {
					logger.error(e.getMessage(), e);
				}

				TimeUtils.sleep(retryInterval);
			} while (System.currentTimeMillis() < endTime);

			return false;
		}

		public void unlockFile() {
			try {
				s3Inst.deleteObject(bucketName, this.key);
			} catch (SdkClientException e) {
				logger.error(e.getMessage(), e);
			}
		}

		private boolean waitLockfileDisappeared(long endTime) {
			boolean bDeadlockTested = false;
			try {
				List<S3ObjectSummary> objects = s3Inst.listObjectsV2(bucketName, this.key).getObjectSummaries();
				while (!objects.isEmpty()) {
					if (!bDeadlockTested) {
						bDeadlockTested = true;
						// Get file creation time to detect deadlock
						S3ObjectSummary lockfileObject = objects.get(0);
						Date lastModified = lockfileObject.getLastModified();
						if (new Date().getTime() - lastModified.getTime() > deadlockInterval) {// longer than 10min
							s3Inst.deleteObject(bucketName, this.key);
							logger.warn("deleted dead lock file");
							return true;
						}
					}

					TimeUtils.sleep(retryInterval);
					if (System.currentTimeMillis() >= endTime) {
						logger.warn("failed to wait for lockfile disappeared");
						return false;
					}

					objects = s3Inst.listObjectsV2(bucketName, this.key).getObjectSummaries();
				}

				return true;
			} catch (AmazonS3Exception e) {
				logger.error(e.getMessage(), e);
				return false;
			}
		}
	}
}
