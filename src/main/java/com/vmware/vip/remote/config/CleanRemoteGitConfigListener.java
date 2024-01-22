/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.remote.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class CleanRemoteGitConfigListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        RunOSGitUtil.deleteLocalRepo();
    }
}
