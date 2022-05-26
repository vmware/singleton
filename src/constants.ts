/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

/**
 * Settings for different stages.
 */
export class Constants {
    public static L10N_COMPONENT_API_ENDPOINT = '/i18n/api/v2/translation';
    public static L10N_STRING_API_ENDPOINT = '/i18n/api/v2/translation';
    public static I18N_API_ENDPOINT = '/i18n/api/v2/formatting/patterns';
    public static L10N_LOCAL_API_ENDPOINT = '/i18n/api/v2/locale';
    public static TRANSLATION_PATTERN = '/i18n/api/v2/combination/translationsAndPattern';
    public static PSEUDO_TAG = '#@';
    public static I18N_ASSETS_PREFIX = 'locale_';
    public static L10N_ASSETS_PREFIX = 'translation_';
    public static ASSETS_SUFFIX = '.json';
    public static SOURCE_LANGUAGE = 'en';
    public static SOURCE_REGION = 'US';
    public static SOURCE_LOCALE = Constants.SOURCE_LANGUAGE + '-' + Constants.SOURCE_REGION;
}
