/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.common;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;

import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.i18n.dto.BaseDTO;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.common.l10n.source.util.IOUtil;
import com.vmware.vip.common.l10n.source.util.PathUtil;

/**
 * This class is designed to test if translation-fetcher
 * module has fetched correct translation resources during
 * build process.
 *
 * @author Brent(zifengl@vmware.com)
 * @date 3/8/2016.
 */
public class TranslationFetcherTest {

    public static void testFetchedResource(String jarPath, String jarName, String resourcePath) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(PathUtil.filterPathForSecurity(jarPath + jarName));
            JarEntry jarEntry = jarFile.getJarEntry(resourcePath);
            assertNotNull("target resource " + resourcePath + " was not packaged.", jarEntry);
            InputStream inputStream = jarFile.getInputStream(jarEntry);
            String translation = IOUtils.toString(inputStream);
            assertNotNull("target resource " + resourcePath + " was empty.", translation);
            System.out.println("target resource " + resourcePath + " was found in package.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeJarFile(jarFile);
        }
    }

    /**
     * Main will be called during test process which is
     * called by gradle. args should be same as the ones
     * which is passed into TranslationFetcherMain.
     * This test case will make sure the bundles are successfully generated after building.
     * @param args
     */
    public static void main(String[] args) {
        if(args.length < 4) {
        	System.out.println(" args.length<2, exist!!!");
        	return;
        }
        String jarPath = args[0];
        String jarName = args[1];
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setProductName(args[2]);
        baseDTO.setVersion(args[3]);
        //String[] components = args[4].split(",");
        //String[] locales = args.length > 5 ? args[5].split(",") : "en".split(",");
        String[] locales = {"en", "zh_CN", "zh_TW", "fr", "de", "ko", "ja"};
        if(args[2].equalsIgnoreCase("devCenter")) {
        	String component = ConstantsFile.DEFAULT_COMPONENT;
            for(String locale : locales) {
                String resourcesPath = "l10n/bundles/"
                        + ResourceFilePathGetter.getProductVersionConcatName(baseDTO)
                        + "/" + component + "/"
                        + ResourceFilePathGetter.getLocalizedJSONFileName(locale);
                testFetchedResource(jarPath, jarName, resourcesPath);
            }
        }
    }
}
