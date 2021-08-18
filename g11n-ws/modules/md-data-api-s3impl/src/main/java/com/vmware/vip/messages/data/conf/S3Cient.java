/**
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.conf;

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
 * the class use S3 configurations to initialize S3 client environment
 */
@Configuration
@Profile("s3")
public class S3Cient {
   private static Logger logger = LoggerFactory.getLogger(S3Cient.class);
   @Autowired
   private S3Config config;

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
         // Verify that the bucket was created by retrieving it and checking its location.
         String bucketLocation =
               s3Client.getBucketLocation(new GetBucketLocationRequest(config.getBucketName()));
         logger.info("Bucket location: {}", bucketLocation);
      }
   }

   public AmazonS3 getS3Client() {
      return s3Client;
   }


}
