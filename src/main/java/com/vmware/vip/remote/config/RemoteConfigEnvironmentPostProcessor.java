/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.remote.config;

import com.vmware.vip.remote.config.model.RemoteConfigModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import  com.vmware.vip.remote.config.constant.RemoteConfigConstant;

import java.util.List;
import java.util.Properties;

public class RemoteConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static String defaultConfigNameProperty = "application.properties";
    private static String defaultConfigNameYml = "application.yml";
    private static String defaultConfigNameYaml = "application.yaml";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {


        environment.getPropertySources().forEach(t ->{
          if(t.getName().contains(defaultConfigNameProperty) || t.getName().contains(defaultConfigNameYml) || t.getName().contains(defaultConfigNameYaml)){
             if (t.getProperty(RemoteConfigConstant.GIT_ENABLE) != null && Boolean.valueOf((String) t.getProperty(RemoteConfigConstant.GIT_ENABLE))){

                 try {
                     RemoteConfigModel model = new RemoteConfigModel();
                     model.setGitUrl((String) t.getProperty(RemoteConfigConstant.GIT_URI));
                     model.setGitBaseDir((String) t.getProperty(RemoteConfigConstant.GIT_BASEDIR));
                     model.setGitBranch((String) t.getProperty(RemoteConfigConstant.GIT_BRANCH));
                     model.setSpringProfilesActive((String) t.getProperty(RemoteConfigConstant.PROFILES_ACTIVE));
                     if (t.getProperty(RemoteConfigConstant.GIT_LOCAL_REPOSITORY) != null){
                         model.setGitLocalRepository((String)t.getProperty(RemoteConfigConstant.GIT_LOCAL_REPOSITORY));
                     }

                     RemoteConfigInitor.initConfig(model);
                     List<Properties> propList = RemoteConfigInitor.formatRemoteConfig();

                     String gitConfigBranchName = "remote git configuration branch Name["+ t.getProperty(RemoteConfigConstant.GIT_BRANCH)+"]";
                     System.out.println(gitConfigBranchName);
                     String remoteConfigName = "remote git configuration[baseConfig]";
                     environment.getPropertySources().addFirst(new PropertiesPropertySource(remoteConfigName, propList.get(0)));
                     String remoteSubConfigName = null;
                     for (int i=1; i<propList.size(); i++ ){
                         remoteSubConfigName = "remote git sub configuration[" +i+ "]";
                         environment.getPropertySources().addAfter(remoteConfigName, new PropertiesPropertySource(remoteSubConfigName, propList.get(i)));
                     }

                 } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Load or set remote configuration failure!");
                 }
             }

             return;
          }
        });

    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE+11;
    }
}
