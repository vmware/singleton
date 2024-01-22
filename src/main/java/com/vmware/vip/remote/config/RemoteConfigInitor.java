/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.remote.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class RemoteConfigInitor {

    public final static String GIT_ENABLE = "spring.profiles.remote.config.enable";
    public final static String GIT_URI = "spring.profiles.remote.config.git.uri";
    public final static String GIT_BRANCH = "spring.profiles.remote.config.git.branch";
    public final static String GIT_BASEDIR= "spring.profiles.remote.config.git.basedir";
    public final static String PROFILES_ACTIVE = "spring.profiles.active";

    public static final String FILE_TYPE_PROPERTIES = ".properties";
    public static final String FILE_TYPE_YML = ".yml";
    public static final String FILE_TYPE_YAML = ".yaml";

    public static List<Properties> formatRemoteConfig(String sshRepo, String filterPath, String branch, String profileActive) throws IOException, InterruptedException {
        File baseDir = RunOSGitUtil.runOSGit(sshRepo, filterPath, branch);
        List<File> files = LocalFileUtil.listFile(baseDir.toPath());
        File baseFile = findBaseConfigFile(files, profileActive);
        List<Properties> result = null;
        if (baseFile != null && (baseFile.getName().endsWith(FILE_TYPE_YML)
                || baseFile.getName().endsWith(FILE_TYPE_YAML))) {
            YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
            yaml.setResources(new FileSystemResource(baseFile));
            result = new ArrayList<>();
            result.add(yaml.getObject());

        } else if (baseFile != null && baseFile.getName().endsWith(FILE_TYPE_PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(baseFile));
            result = new ArrayList<>();
            result.add(properties);
        }

        if (result != null && files.size()>1){
            for (File configFile: files){
                if (!configFile.getName().equals(baseFile.getName())){
                    for(Map.Entry<Object, Object> entry: result.get(0).entrySet()){
                        mapConfigPath(entry, configFile);
                    }
                    if (configFile.getName().endsWith(FILE_TYPE_PROPERTIES)){
                        Properties properties = new Properties();
                        properties.load(new FileInputStream(baseFile));
                        result.add(properties);

                    } else if(configFile.getName().endsWith(FILE_TYPE_YML)
                            || configFile.getName().endsWith(FILE_TYPE_YAML) ){
                        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
                        yaml.setResources(new FileSystemResource(baseFile));
                        result.add(yaml.getObject());

                    }

                }

            }

        }

        return result;

    }


    private static void mapConfigPath(Map.Entry<Object, Object> entry, File configFile){
        if (entry.getValue().toString().endsWith(configFile.getName())){
            if ( entry.getValue().toString().startsWith("classpath:")|| entry.getValue().toString().startsWith("file:")){
                entry.setValue("file:"+configFile.getAbsolutePath());
            }else {
                entry.setValue(configFile.getAbsolutePath());
            }

        }

    }

    private static File findBaseConfigFile(List<File> files, String profileActive) {
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(profileActive + FILE_TYPE_PROPERTIES)
                    || fileName.endsWith(profileActive + FILE_TYPE_YML)
                    || fileName.endsWith(profileActive + FILE_TYPE_YAML)) {
                return file;
            }
        }
        return null;
    }

    
}
