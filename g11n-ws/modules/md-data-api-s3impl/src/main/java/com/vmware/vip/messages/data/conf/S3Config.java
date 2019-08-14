/**
 * 
 *
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 *
 * 
 *
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
      return accessKey;
   }

   public void setAccessKey(String accessKey) {
      this.accessKey = accessKey;
   }

   public String getSecretkey() {
      return secretkey;
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
