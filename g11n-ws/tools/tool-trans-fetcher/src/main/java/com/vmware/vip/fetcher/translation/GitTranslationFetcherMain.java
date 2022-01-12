/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.fetcher.translation;

import java.io.IOException;

import com.vmware.vip.common.l10n.source.dto.SourceBaseDTO;
import com.vmware.vip.common.l10n.source.util.FileUtil;
import com.vmware.vip.common.l10n.source.util.PathUtil;

/**
 * The class represents a main function that copy translation files from local git
 * repository to vI18nManager
 */
public class GitTranslationFetcherMain {

    /**
     * Copy translation files of the specified product or all products from local
     * git repository to vI18nManager
     *
     * @param args The parameters from build commands
     */
    public static void main(String args[]) {
		SourceBaseDTO sourceBaseDTO = new SourceBaseDTO();
		if (args.length == 0 || null == args[0]) {
			return;
		}
		if (args.length == 3) {
			sourceBaseDTO.setProductName(args[1]);
			sourceBaseDTO.setVersion(args[2]);
		}
		String sourcePath = PathUtil.getGitRepoBundlesDiskPath(sourceBaseDTO, args[0]);
		System.out.println("sourcePath:"+sourcePath);
        String managerTargetPath = PathUtil.getManagerBundlesDiskPath(sourceBaseDTO);
        String clientTargetPath = PathUtil.getJavaClientBundlesDiskPath(sourceBaseDTO);
        System.out.println("managerTargetPath:"+managerTargetPath);
        System.out.println("clientTargetPath:"+clientTargetPath);
        copyFile(sourcePath, managerTargetPath);
        copyFile(sourcePath, clientTargetPath);
    }

	public static void copyFile(String sourcePath, String targetPath) {
		boolean result = false;
		try {
			result = FileUtil.copyFiles(sourcePath, targetPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result) {
			System.out.println("copy successfully!");
		} else {
			System.out.println("copy failed!");
		}
	}

}
