"use strict";
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * Settings for different stages.
 */
class Constants {
}
Constants.L10N_COMPONENT_API_ENDPOINT = '/i18n/api/v2/translation';
Constants.L10N_STRING_API_ENDPOINT = '/i18n/api/v2/translation';
Constants.I18N_API_ENDPOINT = '/i18n/api/v2/formatting/patterns';
Constants.L10N_LOCAL_API_ENDPOINT = 'i18n/api/v2/locale';
Constants.PSEUDO_TAG = '#@';
Constants.I18N_ASSETS_PREFIX = 'locale_';
Constants.L10N_ASSETS_PREFIX = 'translation_';
Constants.ASSETS_SUFFIX = '.json';
Constants.SOURCE_LANGUAGE = 'en';
Constants.SOURCE_REGION = 'US';
Constants.SOURCE_LOCALE = Constants.SOURCE_LANGUAGE + '-' + Constants.SOURCE_REGION;
exports.Constants = Constants;
