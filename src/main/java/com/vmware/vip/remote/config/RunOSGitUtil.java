/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.remote.config;

import org.springframework.boot.info.BuildProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;

public class RunOSGitUtil {
    private final static String REMOTE_CONFIG = "remoteConfig";
    private RunOSGitUtil(){}

    public static File runOSGit(String sshRepo, String filterPath, String branch) throws IOException, InterruptedException {
        File file = new File(REMOTE_CONFIG);
        if (file.exists()) {
            LocalFileUtil.deleteFolder(file);
        }
        file.mkdirs();
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            return runWinGit(sshRepo, filterPath, branch, file);
        }else{
            return runLinuxGit(sshRepo, filterPath, branch, file);
        }

    }
    public static File runLinuxGit(String sshRepo, String filterPath, String branch, File dir) throws IOException, InterruptedException {
        String path = dir.getAbsolutePath();
        System.out.println("begin download configuration from remote git: "+sshRepo+" to local");
        String cdCommand = "cd " + path;
        String repoSparse = "git init && git config core.sparsecheckout true";
        String checkPath = "echo " + filterPath.trim() + " >> .git/info/sparse-checkout";

        String addRemoteCommand = "git remote add -f origin " + sshRepo.trim();
        String pullCommand = "git pull origin " + branch;
        String shStr = cdCommand + " && " + repoSparse + " && " + checkPath + "&&" + addRemoteCommand + "&&" + pullCommand;

        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", shStr});
        process.waitFor();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println("cached the configuration to disk: "+path);
        String configBaseDir = path + File.separator + filterPath;
        return new File(configBaseDir);
    }

    public static File runWinGit(String sshRepo, String filterPath, String branch, File dir) throws IOException, InterruptedException {

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


    public static void deleteLocalRepo(){
        File file = new File(REMOTE_CONFIG);
        if (file.exists()) {
            try {
                System.err.println("begin delete remote config cache");
                LocalFileUtil.deleteFolder(file);
                System.err.println("delete remote config cache successfully!");
            }catch (Exception e){
                e.printStackTrace();
                System.err.println("delete remote config cache failure");
            }

        }
    }
}
