/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.remote.config.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * implement run git command in Window OS
 *  @author Shi Hu
 */
public class GitWindowOSServiceImpl implements GitOSService{
    @Override
    public File runOSGit(String sshRepo, String filterPath, String branch, File dir) throws IOException, InterruptedException {
        String path = dir.getAbsolutePath();
        System.out.println("begin download configuration from remote git: "+sshRepo+" to local");
        String cdCommand = "cd " + path;
        String repoSparse = "git init && git config core.sparsecheckout true";
        String checkPath = "echo " + filterPath.trim() + " >> .git\\info\\sparse-checkout";

        String addRemoteCommand = "git remote add -f origin " + sshRepo.trim();
        String pullCommand = "git pull origin " + branch;
        String shStr = cdCommand + " && " + repoSparse + " && " + checkPath + "&&" + addRemoteCommand + "&&" + pullCommand;

        ProcessBuilder pb  = new ProcessBuilder("cmd.exe", "/c", shStr);
        pb.directory(dir);
        Process process =pb.start();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor();
        String configBaseDir = path + File.separator + filterPath;
        return new File(configBaseDir.replaceAll("/", "\\\\"));
    }
}
