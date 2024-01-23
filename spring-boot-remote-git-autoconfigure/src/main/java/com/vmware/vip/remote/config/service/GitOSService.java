/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.remote.config.service;

import java.io.File;
import java.io.IOException;
/**
 * the interface run git command
 *  @author Shi Hu
 */
public interface GitOSService {
    public File runOSGit(String sshRepo, String filterPath, String branch, File dir) throws IOException, InterruptedException;
}
