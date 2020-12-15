//Copyright 2019-2020 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.utils;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
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
	private static long waitS3Operation = 100; // milliseconds
	private static long waitToLock = 10 * 1000L; // 10 seconds

	@Autowired
	private S3Inst s3Inst;

	public String readBundle(String basePath, SingleComponentDTO compDTO) {
		String bundlePath = getBundleFilePath(basePath, compDTO);
		return s3Inst.readObject(bundlePath);
	}

	public boolean writeBundle(String basePath, SingleComponentDTO compDTO) throws JsonProcessingException {
		String bundlePath = getBundleFilePath(basePath, compDTO);
		s3Inst.putObject(bundlePath, compDTO.toPrettyString());
		return true;
	}

	public boolean isBundleExist(String basePath, SingleComponentDTO singleComponentDTO) {
		String bundlePath = getBundleFilePath(basePath, singleComponentDTO);
		return s3Inst.isObjectExist(bundlePath);
	}

	private String getBundleFilePath(String basePath, SingleComponentDTO dto) {
		if (StringUtils.isEmpty(dto.getComponent())) {
			dto.setComponent(ConstantsFile.DEFAULT_COMPONENT);
		}
		String bundlePath = genProductVersionS3Path(basePath, dto.getProductName(), dto.getVersion()) + dto.getComponent()
		+ ConstantsChar.BACKSLASH + ResourceFilePathGetter.getLocalizedJSONFileName(dto.getLocale());

		return s3Inst.normalizePath(bundlePath);
	}

	/**
	 * generate the product version path
	 */
	private static String genProductVersionS3Path(String basePath, String productName, String version) {
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
				s3Inst.putObject(this.key, content);
				TimeUtils.sleep(waitS3Operation); // Wait for a while to let S3 finish writing.

				// Check file content is correct
				try {
					if (content.equals(s3Inst.readObject(this.key))) {
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
				s3Inst.deleteObject(this.key);
			} catch (SdkClientException e) {
				logger.error(e.getMessage(), e);
			}
		}

		private boolean waitLockfileDisappeared(long endTime) {
			boolean bDeadlockTested = false;
			List<S3ObjectSummary> objects = s3Inst.amazonS3.listObjectsV2(s3Inst.bucketName, this.key).getObjectSummaries();
			while (!objects.isEmpty()) {
				if (!bDeadlockTested) {
					bDeadlockTested = true;
					// Get file creation time to detect deadlock
					S3ObjectSummary lockfileObject = objects.get(0);
					Date lastModified = lockfileObject.getLastModified();
					if (new Date().getTime() - lastModified.getTime() > deadlockInterval) {// longer than 10min
						s3Inst.deleteObject(this.key);
						logger.warn("deleted dead lock file");
						return true;
					}
				}

				TimeUtils.sleep(retryInterval);
				if (System.currentTimeMillis() >= endTime) {
					logger.warn("failed to wait for lockfile disappeared");
					return false;
				}

				objects = s3Inst.amazonS3.listObjectsV2(s3Inst.bucketName, this.key).getObjectSummaries();
			}

			return true;
		}
	}
}

