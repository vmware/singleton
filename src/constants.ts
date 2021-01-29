/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { VLocale } from './services/locale.service';

export class VIPServiceConstants {
    public static L10N_COMPONENT_API_ENDPOINT = 'i18n/api/v2/translation';
    public static L10N_STRING_API_ENDPOINT = 'i18n/api/v2/translation';
    public static I18N_API_ENDPOINT = 'i18n/api/v2/formatting/patterns';
    public static L10N_LOCAL_API_ENDPOINT = 'i18n/api/v2/locale';
    public static TRANSLATION_PATTERN = 'i18n/api/v2/combination/translationsAndPattern';
    public static PseudoTag = '#@';
    public static ASSETS_SUFFIX = '.json';
    public static I18N_ASSETS_PREFIX = 'locale_';
    public static L10N_ASSETS_PREFIX = 'translation_';
    public static NAME_SPACE_SEPARATOR = ':';
    public static ENGLISH: VLocale = {
        languageCode: 'en',
        languageName: 'English',
        regionCode: 'US',
        regionName: 'United States'
    };
}
