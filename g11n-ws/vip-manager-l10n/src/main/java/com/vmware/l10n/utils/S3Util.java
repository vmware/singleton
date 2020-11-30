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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.l10n.conf.RsaCryptUtil;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;

@Component("S3Util")
@Profile("s3")
public class S3Util {
	private static Logger logger = LoggerFactory.getLogger(S3Util.class);

	private AmazonS3 s3Inst;

	/**
	 * the s3 password is encryption or not
	 */
	@Value("${s3.password.encryption:false}")
	private boolean encryption;

	/**
	 * the s3 password public key used to decrypt data
	 */
	@Value("${s3.password.publicKey}")
	private String publicKey;

	/**
	 * the s3 access Key
	 */
	@Value("${s3.password.accessKey}")
	private String accessKey;

	/**
	 * the s3 secret key
	 */
	@Value("${s3.password.secretkey}")
	private String secretkey;

	/**
	 * the s3 region name
	 */
	@Value("${s3.region}")
	private String s3Region;

	/**
	 * the s3 bucket Name
	 */
	@Value("${s3.bucketName}")
	private String bucketName;

	private Random random = new Random(System.currentTimeMillis());

	private static long retryInterval = 500; // 500 milliseconds
	private static long deadlockInterval = 10 * 60 * 1000L; // 10 minutes

	/**
	 * initialize the the S3 client environment
	 */
	@PostConstruct
	private void init() {
		s3Inst = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(this.getAccessKey(), this.getSecretkey())))
				.withRegion(s3Region).enablePathStyleAccess().build();
		if (!s3Inst.doesBucketExistV2(bucketName)) {
			s3Inst.createBucket(bucketName);
			// Verify that the bucket was created by retrieving it and checking its
			// location.
			String bucketLocation = s3Inst.getBucketLocation(new GetBucketLocationRequest(bucketName));
			logger.info("Bucket location: {}", bucketLocation);
		}
	}

	public String readBundle(String basePath, SingleComponentDTO compDTO) {
		logger.info("read bundle file: {}/{}/{}/{}", compDTO.getProductName(), compDTO.getVersion(),
				compDTO.getComponent(), compDTO.getLocale());

		String bundlePath = getBundleFilePath(basePath, compDTO);
		String result = null;
		try {
			result = s3Inst.getObjectAsString(bucketName, bundlePath);
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
			String bundlePath = getBundleFilePath(basePath, compDTO);
			s3Inst.putObject(bucketName, bundlePath, convertComponentToString(compDTO));
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			logger.info("end writing bundle file");
		}
	}

	public boolean isBundleExist(String basePath, SingleComponentDTO singleComponentDTO) {
		String bundlePath = getBundleFilePath(basePath, singleComponentDTO);
		return s3Inst.doesObjectExist(bucketName, bundlePath);
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
		String lockfilePath = getLockFile(getBundleFilePath(basePath, compDTO));
		String content = Double.toString(random.nextDouble());

		do {
			if (!waitLockfileDisappeared(lockfilePath, endTime)) {
				return false;
			}

			// Write the lock file
			PutObjectResult putResult = s3Inst.putObject(bucketName, lockfilePath, content);

			// Check file content is correct
			try {
				List<S3VersionSummary> versions = s3Inst.listVersions(bucketName, lockfilePath).getVersionSummaries();
				if (versions.size() > 1 && putResult.getVersionId().equals(versions.get(0).getVersionId())
						&& content.equals(s3Inst.getObjectAsString(bucketName, lockfilePath))) {
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
		String bundlePath = getBundleFilePath(basePath, compDTO);
		String lockFilePath = getLockFile(bundlePath);
		try {
			s3Inst.deleteObject(bucketName, lockFilePath);
		} catch (SdkClientException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String getLockFile(String bundlePath) {
		return bundlePath.substring(0, bundlePath.lastIndexOf('.')) + ".lock";
	}

	private boolean waitLockfileDisappeared(String lockfilePath, long endTime) {
		boolean bDeadlockTested = false;
		try {
			List<S3ObjectSummary> objects = s3Inst.listObjectsV2(bucketName, lockfilePath).getObjectSummaries();
			while (!objects.isEmpty()) {
				if (!bDeadlockTested) {
					bDeadlockTested = true;
					// Get file creation time to detect deadlock
					S3ObjectSummary lockfileObject = objects.get(0);
					Date lastModified = lockfileObject.getLastModified();
					if (new Date().getTime() - lastModified.getTime() > deadlockInterval) {// longer than 10min
						s3Inst.deleteObject(bucketName, lockfilePath);
						logger.info("deleted dead lock file");
						return true;
					}
				}
				sleep(retryInterval);
				if (System.currentTimeMillis() >= endTime) {
					logger.info("time out to lock");
					return false;
				}
				objects = s3Inst.listObjectsV2(bucketName, lockfilePath).getObjectSummaries();
			}

			return true;
		} catch (AmazonS3Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	private void sleep(long millisecond) {
		try {
			TimeUnit.MILLISECONDS.sleep(millisecond);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
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

	public String getAccessKey() {
		if (this.encryption) {
			try {
				return RsaCryptUtil.decryptData(this.getAccessKey(), this.publicKey);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return null;
			}
		} else {
			return this.accessKey;
		}
	}

	public String getSecretkey() {
		if (this.encryption) {
			try {
				return RsaCryptUtil.decryptData(this.secretkey, this.publicKey);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return null;
			}
		} else {
			return this.secretkey;
		}
	}
}
