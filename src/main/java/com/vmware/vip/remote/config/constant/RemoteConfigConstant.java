/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.remote.config.constant;

/**
 * the constant of the git remote configs
 *  @author Shi Hu
 */
public interface RemoteConfigConstant {

    public final static String GIT_ENABLE = "spring.profiles.remote.config.enable";
    public final static String GIT_URI = "spring.profiles.remote.config.git.uri";
    public final static String GIT_BRANCH = "spring.profiles.remote.config.git.branch";
    public final static String GIT_BASEDIR= "spring.profiles.remote.config.git.basedir";
    public final static String GIT_LOCAL_REPOSITORY= "spring.profiles.local.config.cache.dir";
    public final static String GIT_LOCAL_REPOSITORY_DEFAULT_DIR = "remoteConfig";
    public final static String PROFILES_ACTIVE = "spring.profiles.active";


    public static final String FILE_TYPE_PROPERTIES = ".properties";
    public static final String FILE_TYPE_YML = ".yml";
    public static final String FILE_TYPE_YAML = ".yaml";
}
