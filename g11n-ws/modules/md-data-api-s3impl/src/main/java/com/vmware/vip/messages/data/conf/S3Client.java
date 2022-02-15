/**
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.conf;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
/**
 * the class use S3 configurations to initialize S3 client environment
 */
@Configuration
@Profile("s3")
public class S3Client {
	
   private final static Logger LOGGER = LoggerFactory.getLogger(S3Client.class);
   
   /**
	*  session expired time(seconds)
	*/
   private final static int DURATIONSEC = 3600;
   
   /**
    * time error range(microsecond)
    */
   private final static long TIME_ERR_RANGE = 3000;
   
   /**
    * Time errors between current OS systems time with AWS time
    */
   private static long reducedTime;
   
   private static AmazonS3  s3Client;
   private static Credentials sessionCreds;
   @Autowired
   private S3Config config;

   @PostConstruct
   protected void initS3Client() {
	   
	   sessionCreds = getRoleCredentials();
	   s3Client = getAmazonS3();
	   if (!s3Client.doesBucketExistV2(config.getBucketName())) {
	         s3Client.createBucket(config.getBucketName());
	         // Verify that the bucket was created by retrieving it and checking its location.
			String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(config.getBucketName()));
			
			if (StringUtils.isEmpty(bucketLocation)) {
				LOGGER.error("create new bucket failure: {}", config.getBucketName());
			} else {
				LOGGER.info("create new bucket location: {}", bucketLocation);
			}
		}
	   
   }
   
   
   
   public synchronized AmazonS3 getS3Client() {
	   if((sessionCreds.getExpiration().getTime()-System.currentTimeMillis())>reducedTime){
		   return s3Client;
	   }else {
		   sessionCreds = getRoleCredentials();
		   s3Client = getAmazonS3();
		   return s3Client;
	   }
   
   }
   
   
   private synchronized Credentials getRoleCredentials() {
	   
		AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(config.getAccessKey(), config.getSecretkey())) ;
		AWSSecurityTokenService stsClient =  AWSSecurityTokenServiceClientBuilder.standard()
				.withCredentials(awsCredentialsProvider).withRegion(config.getS3Region())
				.build();
		
		AssumeRoleRequest arreq = new AssumeRoleRequest();
		arreq.setDurationSeconds(DURATIONSEC);
		arreq.setRoleArn(config.getRoleArn());
		arreq.setRoleSessionName("SingletonRoleSession");
		
		AssumeRoleResult sessionTokenResult = stsClient.assumeRole(arreq);
		long time = System.currentTimeMillis();
		Credentials  result = sessionTokenResult.getCredentials();
		reducedTime = (result.getExpiration().getTime()-(DURATIONSEC*1000)-time)+TIME_ERR_RANGE;
		return result;
   }
   
   private synchronized AmazonS3 getAmazonS3() {
	   
	   BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
			   sessionCreds.getAccessKeyId(),
			   sessionCreds.getSecretAccessKey(),
			   sessionCreds.getSessionToken());
	   return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
			.withRegion(config.getS3Region()).enablePathStyleAccess().build();
   }
   


}
