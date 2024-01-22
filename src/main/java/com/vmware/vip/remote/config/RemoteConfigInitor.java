/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.remote.config;

import com.vmware.vip.remote.config.model.RemoteConfigModel;
import static com.vmware.vip.remote.config.constant.RemoteConfigConstant.*;

import com.vmware.vip.remote.config.service.LocalFileUtil;
import com.vmware.vip.remote.config.service.RunOSGitUtil;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.util.*;

public class RemoteConfigInitor {

    private static RemoteConfigModel configModel = null;
    public static synchronized boolean initConfig(RemoteConfigModel remoteConfigModel){
      if(configModel == null){
          configModel = remoteConfigModel;
          return true;
      }
      return false;
    }

    public static void deleteLocalRepo(){
        if (configModel != null){
            File file = new File(configModel.getGitLocalRepository());
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

    public static List<Properties> formatRemoteConfig() throws IOException, InterruptedException {

        File baseDir = RunOSGitUtil.runOSGit(configModel);
        List<File> files = LocalFileUtil.listFile(baseDir.toPath());
        File baseFile = findBaseConfigFile(files, configModel.getSpringProfilesActive());
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
