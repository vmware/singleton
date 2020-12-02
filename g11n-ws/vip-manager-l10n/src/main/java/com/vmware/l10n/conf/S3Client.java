/**
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;

/**
 * the configuration of the S3 client
 */
@Configuration
@Profile("s3")
public class S3Client {
	private static Logger logger = LoggerFactory.getLogger(S3Client.class);

	/**
	 * the s3 password is encryption or not
	 */
	@Value("${s3.keysEncryptEnable:false}")
	private boolean encryption;

	/**
	 * the s3 password public key used to decrypt data
	 */
	@Value("${s3.publicKey}")
	private String publicKey;

	/**
	 * the s3 access Key
	 */
	@Value("${s3.accessKey}")
	private String accessKey;

	/**
	 * the s3 secret key
	 */
	@Value("${s3.secretkey}")
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

	private AmazonS3 s3Inst;

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

	public AmazonS3 getS3Client() {
		return s3Inst;
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
