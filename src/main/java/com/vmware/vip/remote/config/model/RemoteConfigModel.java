package com.vmware.vip.remote.config.model;


import com.vmware.vip.remote.config.constant.RemoteConfigConstant;

public class RemoteConfigModel {
    private String gitUrl;
    private String gitBranch;
    private String gitBaseDir;
    private String gitLocalRepository = RemoteConfigConstant.GIT_LOCAL_REPOSITORY_DEFAULT_DIR;
    private String springProfilesActive;

    public String getGitLocalRepository() {
        return gitLocalRepository;
    }

    public void setGitLocalRepository(String gitLocalRepository) {
        this.gitLocalRepository = gitLocalRepository;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getGitBaseDir() {
        return gitBaseDir;
    }

    public void setGitBaseDir(String gitBaseDir) {
        this.gitBaseDir = gitBaseDir;
    }

    public String getSpringProfilesActive() {
        return springProfilesActive;
    }

    public void setSpringProfilesActive(String springProfilesActive) {
        this.springProfilesActive = springProfilesActive;
    }

}
