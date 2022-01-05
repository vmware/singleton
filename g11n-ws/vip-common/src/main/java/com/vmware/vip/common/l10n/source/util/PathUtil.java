/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.l10n.source.util;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.l10n.source.dto.ComponentBaseDTO;
import com.vmware.vip.common.l10n.source.dto.SourceBaseDTO;

/**
 * Path utility class
 * 
 */
public class PathUtil {

    private static HashMap<String, String> map;

    /*
     * Define a map contains special chars, and these chars will be used to fileter the file path
     */
    static{
        map = new HashMap<String, String>();
        for(int i=(int)'a';i<'a'+26;i++){
            map.put(String.valueOf((char)i), String.valueOf((char)i));
            map.put(String.valueOf((char)i).toUpperCase(), String.valueOf((char)i).toUpperCase());
        }
        for(int i = 0; i < 10; i++){
            map.put(String.valueOf(i),String.valueOf(i));
        }
        map.put(ConstantsChar.UNDERLINE, ConstantsChar.UNDERLINE);
        map.put(ConstantsChar.DASHLINE, ConstantsChar.DASHLINE);
        map.put(ConstantsChar.COLON, ConstantsChar.COLON);
        map.put(ConstantsChar.DOT, ConstantsChar.DOT);
        map.put(ConstantsChar.BACKSLASH, ConstantsChar.BACKSLASH);
        map.put(ConstantsChar.AT, ConstantsChar.AT);
        map.put(File.separator, File.separator);
    }

    /**
     * Generate a key by parsing ComponentSourceDTO instance to use in cache
     * 
     * @param ComponentSourceDTO
     * @return the key for cache, e.g devCenter.default.1.0.0
     */
    public static String generateCacheKey(ComponentBaseDTO componentSourceDTO) {
        StringBuilder key = new StringBuilder();
        if (!StringUtils.isEmpty(componentSourceDTO.getProductName())) {
            key.append(componentSourceDTO.getProductName());
        }
        if (!StringUtils.isEmpty(componentSourceDTO.getComponent())) {
            key.append(ConstantsChar.DOT).append(componentSourceDTO.getComponent());
        }
        if (!StringUtils.isEmpty(componentSourceDTO.getVersion())) {
            key.append(ConstantsChar.DOT).append(componentSourceDTO.getVersion());
        }
        return key.toString();
    }

    /**
     * Get the translation files's absolute path of the specified product or all products in local
     * Git repository according to the project's absolute path
     * 
     * @param projectPath this project's absolute path
     * @param sourceBaseDTO the object contains product name and version
     * @return the translation files's absolute path in local Git repository
     */
    public static String getGitRepoBundlesDiskPath(SourceBaseDTO sourceBaseDTO,String jenkinsJobName) {
        String sourcePath = PathUtil.getSourceBasePath() + File.separator + jenkinsJobName + File.separator
                + "workspace" + File.separator + "g11n-translations" + File.separator + ConstantsFile.L10N_BUNDLES_PATH;
        if (!StringUtils.isEmpty(sourceBaseDTO.getProductName())) {
            sourcePath += sourceBaseDTO.getProductName() + File.separator + sourceBaseDTO.getVersion();
        }
        return sourcePath;
    }

	/**
	 * Get the translation files's absolute base path
	 *
	 * @return the translation files's absolute base path in local Git
	 *         repository
	 */
	private static String getSourceBasePath() {
		String projectPath = PathUtil.getProjectAbsolutePath();
		int index = projectPath.indexOf(File.separator + "jobs" + File.separator);
		String sourceBasePath = projectPath.substring(0, index + 6);
		return sourceBasePath;
	}

    /**
     * Get the translation files's absolute path of the specified product or all products in
     * vI18nManager project according to the project's absolute path
     * 
     * @param projectPath this project's absolute path
     * @param sourceBaseDTO the object contains product name and version
     * @return the translation files's absolute path in vI18nManager project
     */
    public static String getManagerBundlesDiskPath(SourceBaseDTO sourceBaseDTO) {
        String targetPath = PathUtil.getTargetBasePath() + File.separator + "g11n-ws" + File.separator
                + "vI18nManager" + File.separator + "build" + File.separator + "resources"
                + File.separator + "main" + File.separator + ConstantsFile.L10N_BUNDLES_PATH;
        if (!StringUtils.isEmpty(sourceBaseDTO.getProductName())) {
            targetPath +=  sourceBaseDTO.getProductName() + File.separator + sourceBaseDTO.getVersion();
        }
        return targetPath;
    }

	/**
	 * Get the translation files's absolute path of the specified product or all
	 * products in vIPJavaClient project according to the project's absolute path
	 * @param projectPath this project's absolute path
	 * @param sourceBaseDTO the object contains product name and version
	 * @return the translation files's absolute path in vIPJavaClient project
	 */
	public static String getJavaClientBundlesDiskPath(SourceBaseDTO sourceBaseDTO) {
		String targetPath = PathUtil.getTargetBasePath() + File.separator + "g11n-ws" + File.separator
				+ "clients" + File.separator + "java" + File.separator + "vIPJavaClient" + File.separator
				+ "build" + File.separator + "resources" + File.separator + "main" + File.separator
				+ ConstantsFile.L10N_BUNDLES_PATH;
		if (!StringUtils.isEmpty(sourceBaseDTO.getProductName())) {
			targetPath += sourceBaseDTO.getProductName() + File.separator + sourceBaseDTO.getVersion();
		}
		return targetPath;
	}

	/**
	 * Get the translation files's absolute base path
	 *
	 * @return the translation files's absolute base path in vI18nManager project
	 *
	 */
	private static String getTargetBasePath() {
		String projectPath = PathUtil.getProjectAbsolutePath();
		int index = projectPath.indexOf(File.separator + "g11n-ws" + File.separator);
		String targetBasePath = projectPath.substring(0, index);
		return targetBasePath;
	}

    /**
     * Get the project's absolute path
     * 
     * @return the project's absolute path
     */
    public static String getProjectAbsolutePath() {
        return new File(ConstantsChar.EMPTY).getAbsolutePath();
    }

    /**
     * Filter path for security.
     * <p>
     * When use Fortify to scan vip project code, Fortify will warn a "Path Manipulation" issue.
     * <p>
     * e.g. File f = new File(jsonFileName);
     * <p>
     * Fortify will tell "Attackers can control the filesystem path argument to File() at
     * ResourceFilePathGetter.java line 116, which allows them to access or modify otherwise
     * protected files."
     * 
     * @param path to be filtered
     * @return filtered path
     */
    public static String filterPathForSecurity(String path){
        path = path.replace(ConstantsChar.DOUBLE_DOT, "");
        String temp = "";
        for (int i = 0; i < path.length(); i++) {
            if (map.get(String.valueOf(path.charAt(i))) != null) {
                temp += map.get(String.valueOf(path.charAt(i)));
            }
        }
        return temp;
    }

    /**
     * Unit test main
     * 
     */
    public static void main(String[] args) {
        //e.g 1: path1 = "D:\..workspace_g11n\g11n-translations\l10n\bundles\SIM\1.0.0\default\messages_zh_CN.json"
        //e.g 2: path2 = "D:/gitrepo/vmware/release-1.0.0/g11n-service/g11n-ws\\vI18nManager\\l10n\\bundles\\vCG/2.0.0/cim/message_zh_CN.json";
        //e.g 3: path3 = "D:\\gitrepo\\vmware\\release-1.0.0\\g11n-service\\g11n-ws\\vI18nManager\\l10n\\bundles\\vCG\\2.0.0\\cim\\message_zh_CN.json";
        String path1 = "D:" + File.separator + "..workspace_g11n" + File.separator
                + "g11n-translations" + File.separator + "l10n" + File.separator + "bundles"
                + File.separator + "devCenter" + File.separator + "1.0.0" + File.separator
                + "default" + File.separator + "messages_zh_CN.json";
        String result = PathUtil.filterPathForSecurity(path1);
        System.out.println("after: " + result);
    }

}
