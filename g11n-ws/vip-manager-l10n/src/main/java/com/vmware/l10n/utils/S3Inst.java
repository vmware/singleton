//Copyright 2019-2020 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.utils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.vmware.l10n.conf.S3Client;
import com.vmware.vip.common.constants.ConstantsChar;

@Component
@Profile("s3")
public class S3Inst {

	@Autowired
	private S3Client client;

	/**
	 * the s3 bucket Name
	 */
	@Value("${s3.bucketName}")
	public String bucketName;

	public AmazonS3 amazonS3;

	@PostConstruct
	private void init() {
		this.amazonS3 = client.getS3Client();
	}
	
	public String readObject(String key) {
		return amazonS3.getObjectAsString(bucketName, normalizePath(key));
	}

	public boolean isObjectExist(String key) {
		return amazonS3.doesObjectExist(bucketName, normalizePath(key));
	}
	
	public void putObject(String key, String content) {
		amazonS3.putObject(bucketName, normalizePath(key), content);
	}

	public void deleteObject(String key) {
		amazonS3.deleteObject(bucketName, normalizePath(key));
	}

	public String normalizePath(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path.replace("\\", ConstantsChar.BACKSLASH);
	}
}