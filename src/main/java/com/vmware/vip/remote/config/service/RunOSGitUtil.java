/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.remote.config.service;

import com.vmware.vip.remote.config.model.RemoteConfigModel;

import java.io.File;
import java.io.IOException;

public class RunOSGitUtil {

    private RunOSGitUtil(){}

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
