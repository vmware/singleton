/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This class is the initial class of springboot service.
 */
@SpringBootApplication(scanBasePackages = { "com.vmware.vip", "com.vmware.l10n" })
@EnableScheduling
@ServletComponentScan("com.vmware.l10n")
public class BootApplication {


    /**
     * The entrance to start the springboot application.
     *
     * @param args
     * @throws Exception
    // System.setProperty(net.sf.ehcache.CacheManager.ENABLE_SHUTDOWN_HOOK_PROPERTY, "true");
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
		System.setProperty("org.apache.catalina.connector.CoyoteAdapter.ALLOW_BACKSLASH", "true");
        SpringApplication.run(BootApplication.class, args);
    }
}
