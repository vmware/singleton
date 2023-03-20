/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.common;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.l10n.source.dto.SourceBaseDTO;
import com.vmware.vip.common.l10n.source.util.FileUtil;
import com.vmware.vip.common.l10n.source.util.PathUtil;

/**
 * Git Translation Fetcher Main
 *
 * @author <a href="mailto:linr@vmware.com">Colin Lin</a>
 */
public class GitTranslationFetcherMainForTest {

    /**
     * copy translation files of the specified product or all products from local git repository to
     * vI18nManager test path
     * @param args the parameters from build commands
     */
    public static void main(String args[]) {
		SourceBaseDTO sourceBaseDTO = new SourceBaseDTO();
		if (args.length == 0 || null == args[0]) {
			System.out.println("args size is 0 exit");
			return;
		}
		if (args.length == 3) {
			
			System.out.println("args size is 3 arg0:"+args[0]+"arg1:"+args[1]+"arg2:"+args[2]);
			sourceBaseDTO.setProductName(args[1]);
			sourceBaseDTO.setVersion(args[2]);
		}
		String sourcePath = PathUtil.getGitRepoBundlesDiskPath(sourceBaseDTO, args[0]);
        String targetPath = getBundlesToDiskPath(sourceBaseDTO);
        boolean result = false;
        try {
            result = FileUtil.copyFiles(sourcePath, targetPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (result) {
            System.out.println("Translation for API test copy successfully!");
        } else {
            System.out.println("Translation for API test copy failed!");
        }
    }

    /**
     * get the translation files's absolute path of the specified product or all products in
     * vI18nManager project according to the project's absolute path
     * @param sourceBaseDTO the object contains product name and version
     * @return the translation files's absolute path in vI18nManager project
     */
    public static String getBundlesToDiskPath(SourceBaseDTO sourceBaseDTO) {
        String targetPath = ConstantsChar.EMPTY;
        String projectPath = PathUtil.getProjectAbsolutePath();
        int index = projectPath.indexOf(File.separator + "g11n-ws" + File.separator);
        if (StringUtils.isEmpty(sourceBaseDTO.getProductName())) {
            targetPath = projectPath.substring(0, index) + ConstantsForTest.BUNDLES_TARGET_FOLDER_ForTest;
        } else {
            targetPath = projectPath.substring(0, index) + ConstantsForTest.BUNDLES_TARGET_FOLDER_ForTest
                    + sourceBaseDTO.getProductName() + File.separator
                    + sourceBaseDTO.getVersion();
        }
        return targetPath;
    }

}
