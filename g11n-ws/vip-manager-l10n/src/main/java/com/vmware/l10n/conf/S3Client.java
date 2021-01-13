/**
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private S3Cfg config;

	private AmazonS3 s3Client;

	/**
	 * initialize the the S3 client environment
	 */
	@PostConstruct
	private void init() {
		s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(config.getAccessKey(), config.getSecretkey())))
				.withRegion(config.getS3Region()).enablePathStyleAccess().build();
		if (!s3Client.doesBucketExistV2(config.getBucketName())) {
			s3Client.createBucket(config.getBucketName());
			// Verify that the bucket was created by retrieving it and checking its
			String bucketLocation =
					s3Client.getBucketLocation(new GetBucketLocationRequest(config.getBucketName()));
			logger.info("Bucket location: {}", bucketLocation);
		}
	}

	public AmazonS3 getS3Client() {
		return s3Client;
	}

	public String readObject(String key) {
		return s3Client.getObjectAsString(config.getBucketName(), normalizePath(key));
	}

    public void putObject(String key, String content) {
        s3Client.putObject(config.getBucketName(), normalizePath(key), content);
    }

	public void deleteObject(String key) {
		s3Client.deleteObject(config.getBucketName(), normalizePath(key));
	}

	public boolean isObjectExist(String key) {
		return s3Client.doesObjectExist(config.getBucketName(), normalizePath(key));
	}

	public String normalizePath(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}
}
