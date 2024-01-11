/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import com.vmware.vip.common.cache.SingletonCache;
import com.vmware.vip.common.cache.SingletonCacheImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    private static Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    @Value("${singleton.cache.enable:false}")
    private boolean singletonCacheEnable;

    @Bean
    public SingletonCache initSingletonCache(){
        logger.info("Begin init singleton cache with configuration:singleton.cache.enable = {}", singletonCacheEnable);
        SingletonCacheImpl singletonCache = new SingletonCacheImpl(this.singletonCacheEnable);
        logger.info("Init singleton cache successfully");
        return singletonCache;
    }

}
