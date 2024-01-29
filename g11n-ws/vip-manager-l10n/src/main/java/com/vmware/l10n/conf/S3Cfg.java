/**
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.vmware.vip.common.constants.ConstantsFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.vmware.vip.common.utils.RsaCryptUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * the configuration of the S3 client
 */
@Configuration
@Profile("s3")
public class S3Cfg {
	private static Logger logger = LoggerFactory.getLogger(S3Cfg.class);
	
	/**
	 * the s3 password is encryption or not
	 */
	@Value("${s3.keysEncryptEnable:false}")
	private Boolean encryption;

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
	 * the s3 buncket Name
	 */
	@Value("${s3.bucketName}")
	private String bucketName;
	
	/**
	 * the s3 RoleArn
	 */
	
	@Value("${s3.roleArn}")
	private String roleArn;
	
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

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
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
		try {
			if (this.publicKey.startsWith(ConstantsFile.CLASS_PATH_PREFIX)
					|| this.publicKey.startsWith(ConstantsFile.FILE_PATH_PREFIX)) {
				Resource resource = new PathMatchingResourcePatternResolver().getResource(this.publicKey);
				String content = RsaCryptUtils.getPublicKeyStrFromInputStream(resource.getInputStream());
				logger.debug("public key: {}", content);
				return content;
			} else {
				File file = new File(this.publicKey);
				if (file.exists()) {
					String content = null;

					content = RsaCryptUtils.getPublicKeyStrFromInputStream(new FileInputStream(file));
					logger.debug("public key: {}", content);
					return content;
				} else {
					logger.error("not found public key file: {}", file.getAbsoluteFile());
					return null;
				}
			}
		} catch (IOException e) {
            logger.error(e.getMessage(), e);
			return null;
        }

    }

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getRoleArn() {
		return roleArn;
	}
	
}
