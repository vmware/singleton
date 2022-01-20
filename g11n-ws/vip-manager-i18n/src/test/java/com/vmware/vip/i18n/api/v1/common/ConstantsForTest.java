/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.common;

import java.io.File;

import com.vmware.vip.common.constants.ConstantsFile;

/**
 * Constants
 *
 * @author Colin Lin
 */
public class ConstantsForTest {

    public static final String BUNDLES_TARGET_FOLDER_ForTest = File.separator + "g11n-ws"
            + File.separator + "vip-manager-i18n" + File.separator + "build" + File.separator
            + "classes" + File.separator + "test" + File.separator
            + ConstantsFile.L10N_BUNDLES_PATH;

    // URI for vIP API
    public static final String KeyAPIURI = "/i18n/api/v1/security/authentication/key?productName=vCG&version=2.0.0&userID=huihuiw";
    public static final String AuthenticationAPIURI = "/i18n/api/v1/security/authentication?productName=vCG&version=2.0.0&userID=huihuiw&key=F8D2B8303C2D1AD63A334ECB3B53572565BD9AB5573B0FE817E4C51276215B77";

    public static final String DateAPIURI = "/i18n/api/v1/date/localizedDate?longDate=1467185266089&locale=fr_FR&pattern=yMMMM";

    public static final String BrowserLocaleAPIURI = "/i18n/api/v1/locale/browserLocale";
    public static final String NormalizedBrowserLocaleAPIURI = "/i18n/api/v1/locale/normalizedBrowserLocale";

    public static final String SingleComponentTranslationAPIURI = "/i18n/api/v1/translation/component?productName=vCG&component=cim&version=2.0.0&locale=zh_CN";
    public static final String MultipleComponentsTranslationAPIURI = "/i18n/api/v1/translation/components?productName=vCG&components=cim,common&version=2.0.0&locales=ja_JP,zh_CN";

    public static final String ComponentNameListAPIURI = "/l10n/api/v1/bundles/components?productName=logInsight&version=4.6.0";
    public static final String SupportedLocalesAPIURI = "/i18n/api/v1/translation/product/logInsight/version/4.6.0/supportedLocales";
    public static final String ProductTranslationAPIURI = "/i18n/api/v1/translation?productName=logInsight&version=4.6.0";

    public static final String StringTranslationAPIURI = "/i18n/api/v1/translation/string?productName=vCG&version=2.0.0&component=cim&key=cim_intro&source=cim_intro&locale=zh_CN";

    public static final String SingleComponentTranslationAPIURI_REST = "/i18n/api/v1/translation/product/vCG/component/server?version=2.0.0&locale=zh_CN";
    public static final String MultipleComponentsTranslationAPIURI_REST = "/i18n/api/v1/translation/product/vCG/components/software,sra,vco,vdm,vfrc,vrops?version=2.0.0&locales=ja_JP,zh_CN";

    public static final String StringTranslationContainComponentAPIURI = "/i18n/api/v1/translation/product/vCG/component/cim/key/Product_Release_Version?source=Product Release Version&version=2.0.0&locale=zh_CN";
    public static final String StringTranslationExclusiveComponentAPIURI = "/i18n/api/v1/translation/product/vCG/key/verified_supported_partners?source=Looking for products verified and supported by partners?&version=2.0.0&locale=zh_CN&sourceFormat=HTML";

    public static final String StringUpdateTranslationAPIURI = "/i18n/api/v1/translation/product/vCG/version/2.0.0";
    public static final String StringValidateUpdateTranslationAPIURI = "/i18n/api/v1/translation/string?productName=vCG&version=2.0.0&component=cim&key=Partner_Name&source=Partner Name&locale=zh_CN";

    public static final String CreateSourceAPIURI = "/i18n/api/v1/translation/product/vCG/component/cpu/sources?version=2.0.0&sourceFormat=&collectSource=false&pseudo=false";
    public static final String GetTranslationBySourceAPIURI = "/i18n/api/v1/translation/product/vCG/component/cpu/sources?version=2.0.0&locale=zh_CN&source=Product Release Version&sourceFormat=&collectSource=false&pseudo=false";
    // constants for data type
    public static final String JSON = "json";
    public static final String DATA = "data";

    // constants for encoding
    public static final String UTF8 = "UTF-8";

    // constants for characters
    public static final String QuestionMark = "?";

    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PUT = "PUT";

    public static final String JA_JP = "ja_JP";
    public static final String ZH_HANS = "zh-Hans";

    public static final String VCG = "vCG";
    public static final String VERSION = "2.0.0";
    public static final String GRM = "GRM";
    public static final String CIM = "cim";

}
