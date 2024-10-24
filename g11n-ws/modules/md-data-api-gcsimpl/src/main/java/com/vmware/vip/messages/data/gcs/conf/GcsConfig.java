/**
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.gcs.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


/**
 * the configuration of the gcs client
 */
@Configuration
@Profile("gcs")
public class GcsConfig {

    /**
     * the gcs region name
     */
    @Value("${gcs.region:us-east1}")
    private String gcsRegion;
    
    /**
     * the gcs project id
     */
    @Value("${gcs.projectId}")
    private String projectId;
    
    /**
     * the gcs buncket Name
     */
    @Value("${gcs.bucketName}")
    private String bucketName;
    
    @Value("${allow.list.path.bucketName:}")
    private String allowListBucketName;

    public String getProjectId() {
        return projectId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getGcsRegion() {
        return gcsRegion;
    }

    public String getAllowListBucketName() {
        if (this.allowListBucketName != null && (!this.allowListBucketName.isBlank())){
            return this.allowListBucketName;
        }else {
            return this.bucketName;
        }
    }
}
