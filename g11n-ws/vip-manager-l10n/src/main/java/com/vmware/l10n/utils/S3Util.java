//Copyright 2019-2020 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.util.S3Utils;
import com.vmware.vip.util.conf.S3Client;

@Component("S3Util")
@Profile("s3")
public class S3Util {
	private static Logger logger = LoggerFactory.getLogger(S3Util.class);

	@Autowired
	private S3Client s3Client;

	private static AmazonS3 s3;

	@Value("${s3.bucketName}")
	private String bucketName;

	private Random random = new Random(System.currentTimeMillis());

	private static long retryInterval = 500; // 500 milliseconds
	private static long deadlockInterval = 10 * 60 * 1000L; // 10 minutes

	@PostConstruct
	private void init() {
		s3 = s3Client.getS3Client();
	}

	public String readBundle(String basePath, SingleComponentDTO compDTO) {
		logger.info("read bundle file: {}/{}/{}/{}", compDTO.getProductName(), compDTO.getVersion(),
				compDTO.getComponent(), compDTO.getLocale());

		String bundlePath = S3Utils.getBundleFilePath(basePath, compDTO);
		String result = null;
		try {
			result = s3.getObjectAsString(bucketName, bundlePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.info("end reading bundle file");
		return result;
	}

	public boolean writeBundle(String basePath, SingleComponentDTO compDTO) {
		logger.info("write bundle file: {}/{}/{}/{}", compDTO.getProductName(), compDTO.getVersion(),
				compDTO.getComponent(), compDTO.getLocale());

		try {
			String bundlePath = S3Utils.getBundleFilePath(basePath, compDTO);
			s3.putObject(bucketName, bundlePath, convertComponentToString(compDTO));
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			logger.info("end writing bundle file");
		}
	}

	public boolean isBundleExist(String basePath, SingleComponentDTO singleComponentDTO) {
		String bundlePath = S3Utils.getBundleFilePath(basePath, singleComponentDTO);
		return s3.doesObjectExist(bucketName, bundlePath);
	}

	public String convertComponentToString(SingleComponentDTO compDTO) throws JsonProcessingException {
		Map<String, Object> json = new HashMap<>();
		json.put(ConstantsKeys.COMPONENT, compDTO.getComponent());
		json.put(ConstantsKeys.lOCALE, compDTO.getLocale());
		json.put(ConstantsKeys.MESSAGES, compDTO.getMessages());
		json.put(ConstantsKeys.ID, compDTO.getId());
		return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json);
	}

	public boolean lockBundleFile(String basePath, SingleComponentDTO compDTO, long waittime) {
		long endTime = System.currentTimeMillis() + waittime;
		String lockfilePath = getLockFile(S3Utils.getBundleFilePath(basePath, compDTO));
		boolean bDeadlockTested = false;
		String content = Double.toString(random.nextDouble());

		do {
			List<S3ObjectSummary> objects = s3.listObjectsV2(bucketName, lockfilePath).getObjectSummaries();
			while (!objects.isEmpty()) {
				try {
					if (!bDeadlockTested) {
						bDeadlockTested = true;
						// Get file creation time to detect deadlock
						S3ObjectSummary lockfileObject = objects.get(0);
						Date lastModified = lockfileObject.getLastModified();
						if (new Date().getTime() - lastModified.getTime() > deadlockInterval) {// longer than 10min
							s3.deleteObject(bucketName, lockfilePath);
							logger.info("deleted dead lock file");
							break;
						}
					}
					sleep(retryInterval);
					if (System.currentTimeMillis() >= endTime) {
						logger.info("time out to lock");
						return false;
					}
					objects = s3.listObjectsV2(bucketName, lockfilePath).getObjectSummaries();
				} catch (AmazonS3Exception e) {
					logger.error(e.getMessage(), e);
				}
			}

			// Write the lock file
			PutObjectResult putResult = s3.putObject(bucketName, lockfilePath, content);

			// Check file content is correct
			try {
				List<S3VersionSummary> versions = s3.listVersions(bucketName, lockfilePath).getVersionSummaries();
				if (versions.size() > 1 && putResult.getVersionId().equals(versions.get(0).getVersionId())
						&& content.equals(s3.getObjectAsString(bucketName, lockfilePath))) {
					return true;
				}
			} catch (AmazonS3Exception e) {
				logger.error(e.getMessage(), e);
			}

			sleep(retryInterval);
		} while (System.currentTimeMillis() < endTime);

		return false;
	}

	public void unlockBundleFile(String basePath, SingleComponentDTO compDTO) {
		String bundlePath = S3Utils.getBundleFilePath(basePath, compDTO);
		String lockFilePath = getLockFile(bundlePath);
		try {
			s3.deleteObject(bucketName, lockFilePath);
		} catch (SdkClientException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String getLockFile(String bundlePath) {
		return bundlePath.substring(0, bundlePath.lastIndexOf('.')) + ".lock";
	}

	private void sleep(long millisecond) {
		try {
			TimeUnit.MILLISECONDS.sleep(millisecond);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
