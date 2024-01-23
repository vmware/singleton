/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.remote.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * spring boot application listener with ContextClosedEvent to delete the local cache repository dir
 * @author Shi Hu
 */
public class CleanRemoteGitConfigListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        RemoteConfigInitor.deleteLocalRepo();
    }
}
