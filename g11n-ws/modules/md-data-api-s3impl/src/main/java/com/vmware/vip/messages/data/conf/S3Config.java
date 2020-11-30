/**
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * the configuration of the S3 client
 */
@Configuration
public class S3Config {

	public String getAccessKey() {
		if (this.encryption) {
			try {
				return RsaCryptUtils.decryptData(this.accessKey, this.publicKey);
			} catch (Exception e) {
				return null;
			}
		} else {
			return this.accessKey;
		}
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretkey() {
		if (this.encryption) {
			try {
				return RsaCryptUtils.decryptData(this.secretkey, this.publicKey);
			} catch (Exception e) {
				return null;
			}
		} else {
			return this.secretkey;
		}
	}

	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getS3Region() {
		return s3Region;
	}

	public void setS3Region(String s3Region) {
		this.s3Region = s3Region;
	}

	public Boolean isEncryption() {
		return encryption;
	}

	public void setEncryption(Boolean encryption) {
		this.encryption = encryption;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * the s3 password is encryption or not
	 */
	@Value("${s3.keysEncryptEnable:false}")
	private Boolean encryption;
	

	/**
	 * the s3 password public key use to decrypt data
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
	 * the s3 buncket Name
	 */
	@Value("${s3.bucketName}")
	private String bucketName;

}
