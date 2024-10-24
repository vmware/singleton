/**
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.gcs.conf;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

/**
 * the class use gcs configurations to initialize gcs client environment
 */
@Configuration
@Profile("gcs")
public class GcsClient {
    
   private final static Logger LOGGER = LoggerFactory.getLogger(GcsClient.class);
   
   private static Storage  gcsStorage;

   @Autowired
   private GcsConfig gcsConfig;

   @PostConstruct
   protected void initGcsClient() {
       
       gcsStorage = StorageOptions.newBuilder()
               .setProjectId(gcsConfig.getProjectId())
               .build()
               .getService();
       if ( gcsStorage.get(gcsConfig.getBucketName()) == null ) {
            Bucket bucket = gcsStorage.create(BucketInfo.of(gcsConfig.getBucketName()));
             // Verify that the bucket was created by retrieving it and checking its location.
            if (!bucket.exists()) {
                LOGGER.error("create new bucket failure: {}", gcsConfig.getBucketName());
            } else {
                LOGGER.info("create new bucket location: {}", bucket.asBucketInfo().getLocation());
            }
        } else {
            LOGGER.info("Bucket {} already exists", gcsConfig.getBucketName());
        }
       
   }
   
   public synchronized Storage getGcsStorage() {
        return gcsStorage;
   }
   
}
