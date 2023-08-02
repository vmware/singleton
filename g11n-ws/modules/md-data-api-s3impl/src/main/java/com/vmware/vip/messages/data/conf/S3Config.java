/**
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.conf;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.utils.RsaCryptUtils;

/**
 * the configuration of the S3 client
 */
@Configuration
@Profile("s3")
public class S3Config {
	private static Logger logger = LoggerFactory.getLogger(S3Config.class);
	/**
	 * the s3 password is encryption or not
	 */
	@Value("${s3.keysEncryptEnable:false}")
	private Boolean encryption;
	
	/**
	 * the s3 password public key used to decrypt data
	 */
	@Value("${s3.publicKey:#}")
	private String publicKey;
	

	/**
	 * the s3 password public key used to decrypt data
	 */
	@Value("${secret.rsa.publicKeyPath:#}")
	private String publicKeyPath;

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
	
	/**
	 * the s3 RoleArn
	 */
	
	@Value("${s3.roleArn}")
	private String roleArn;

	@Value("${allow.list.path.bucketName:}")
	private String allowListBucketName;
	
	
	
	public String getAccessKey() {
		if (this.encryption) {
			try {
          logger.debug("accessKey: {}", this.accessKey);
				return RsaCryptUtils.decryptData(this.accessKey, this.getPublicKey());
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
				 logger.debug("secretkey: {}", this.secretkey);
				return RsaCryptUtils.decryptData(this.secretkey, this.getPublicKey());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return null;
			}
		} else {
			return this.secretkey;
		}
	}

	public String getBucketName() {
		return bucketName;
	}

	public String getS3Region() {
		return s3Region;
	}

	public Boolean isEncryption() {
		return encryption;
	}

	public String getPublicKey() {
		String filePath = "";
		if(this.publicKey.startsWith(ConstantsChar.POUND)) {
			filePath = this.publicKeyPath;
		}else {
			filePath = this.publicKey;
		}
		File file = new File(filePath);
		if(file.exists()) {
			String content = RsaCryptUtils.getPublicKeyStrFromFile(file);
			logger.debug("public key: {}", content);
			return content;
			
		}else {
			 logger.error("not found public key file: {}", file.getAbsoluteFile());
		 return null;	
		}
	}

	public String getRoleArn() {
		return roleArn;
	}

	public String getAllowListBucketName() {
		if (this.allowListBucketName != null && (!this.allowListBucketName.isBlank())){
			return this.allowListBucketName;
		}else {
			return this.bucketName;
		}
	}
}
