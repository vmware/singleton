/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.remote.config.service;

import com.vmware.vip.remote.config.model.RemoteConfigModel;

import java.io.File;
import java.io.IOException;

/**
 * Base the OS init different run git command OS service implement
 */
public class RunOSGitUtil {

    private RunOSGitUtil(){}

    /**
     * Base the OS, load the config from remote git repository
     * @param configModel
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static File runOSGit(RemoteConfigModel configModel) throws IOException, InterruptedException {
        File file = new File(configModel.getGitLocalRepository());
        if (file.exists()) {
            LocalFileUtil.deleteFolder(file);
        }
        file.mkdirs();
        GitOSService gitOSService = null;
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            gitOSService = new GitWindowOSServiceImpl();
        }else{
            gitOSService = new GitLinuxOSServiceImpl();

        }
        return gitOSService.runOSGit(configModel.getGitUrl(), configModel.getGitBaseDir(), configModel.getGitBranch(), file);
    }


}
