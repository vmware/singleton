/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.constants;

import java.io.File;

/**
 * This class define some constant path used in handling translation file.
 * 
 */
public class ConstantsFile {

    // Constants for coping resources to build
    public static final String L10N_BUNDLES_PATH = "l10n" + File.separator + "bundles" + File.separator;

    // Constants for generating local resources files
    public static final String FILE_TPYE_JSON = ".json";
    public static final String FILE_TPYE_PROPERTIES = ".properties";
    public static final String LOCAL_FILE_SUFFIX = "messages";
    public static final String GENERATED_MAIN_FOLDER = "main" + File.separator + "resources"
            + File.separator + L10N_BUNDLES_PATH;
    public static final String GENERATED_FOLDER = "src-generated" + File.separator
            + GENERATED_MAIN_FOLDER;

    public static final String DEFAULT_COMPONENT = "default";

    public static final String CREATION_INFO = "creation.json";

    public static final String VERSION_FILE = "version.json";
    
    public static final String ALLOW_LIST_FILE = "bundle.json";

    public static final String FILE_TYPE_SVG = ".svg";

    public static final String FILE_PATH_PREFIX="file:";

    public static final String CLASS_PATH_PREFIX="classpath:";

}
