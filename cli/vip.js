/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const axios = require('axios');

class VIPConfig {
    get TRANSLATION_PREFIX() {
        return 'translation_';
    }
    get PATTERN_PREFIX() {
        return 'locale_';
    }
    get SUPPORT_LANGUAGES() {
        return 'languages_';
    }
    get SUPPORT_REGIONS() {
        return 'regions_';
    }
    get L10N_LOCAL_API_ENDPOINT() {
        return 'i18n/api/v2/locale';
    }
    get L10N_COMPONENT_API_ENDPOINT() {
        return 'i18n/api/v2/translation';
    }
    get I18N_API_ENDPOINT() {
        return 'i18n/api/v2/formatting/patterns/locales';
    }
    get DEFAULT_SCOPE() {
        return "dates,numbers,plurals,measurements,currencies";
    }
    constructor(host, product, version, component, scope) {
        this.host = host;
        this.product = product;
        this.version = version;
        this.component = component;
        this.scope = scope;
    }
}
class VIPConfigLoadI18nPackage extends VIPConfig {
    constructor(host, product, version, component, scope, specifiedPackage) {
        super(host, product, version, component, scope)
        this.setSpecifiedPackage(specifiedPackage);
    }
    setSpecifiedPackage(specifiedPackage) {
        this.languagesPackage = false;
        this.regionsPackage = false;
        this.translationPackage = false;
        if (!specifiedPackage) { // when without --package download all
            this.languagesPackage = true;
            this.regionsPackage = true;
            this.translationPackage = true;
        } else { // when with --package, only download specified one(s)
            let packageArr = specifiedPackage.split(',');
            packageArr.forEach(pk => {
                if (pk === 'translation') {
                    this.translationPackage = true;
                }
                if (pk === 'regions') {
                    this.regionsPackage = true;
                }
                if (pk === 'languages') {
                    this.languagesPackage = true;
                }
            });
        }
    }
}

class VIPService {
    constructor(vipConfig, logger, token) {
        this.logger = logger;
        this.vipConfig = vipConfig;
        this.token = token;
    }
    loadSupportedLanguages(displayName) {
        const url = this.vipConfig.host +
            `/${this.vipConfig.L10N_LOCAL_API_ENDPOINT}` +
            '/supportedLanguageList?' +
            `productName=${this.vipConfig.product}` +
            `&version=${this.vipConfig.version}` +
            `&displayLanguage=${displayName}`;
        this.logger.debug('loadSupportedLanguages arguments %o', arguments);
        this.logger.debug('loadSupportedLanguages url is ', url);
        return this.get(url, this.token);
    }
    loadSupportedRegions(displayName) {
        const url = this.vipConfig.host +
            `/${this.vipConfig.L10N_LOCAL_API_ENDPOINT}` +
            '/regionList?' +
            `supportedLanguageList=${displayName}`;
        this.logger.debug('loadSupportedRegions arguments %o', arguments);
        this.logger.debug('loadSupportedRegions url is ', url);
        return this.get(url, this.token);
    }
    loadTranslation(locale) {
        const url = this.vipConfig.host +
            `/${this.vipConfig.L10N_COMPONENT_API_ENDPOINT}` +
            `/products/${this.vipConfig.product}` +
            `/versions/${this.vipConfig.version}` +
            `/locales/${locale}` +
            `/components/${this.vipConfig.component}` +
            `?pseudo=false`;
        this.logger.debug('loadTranslation arguments %o', arguments);
        this.logger.debug('loadTranslation url is', url);
        return this.get(url, this.token);
    }
    loadPattern(locale) {
        const url = this.vipConfig.host +
            `/${this.vipConfig.I18N_API_ENDPOINT}` +
            `/${locale}` +
            `?scope=${this.vipConfig.scope}`;
        this.logger.debug('loadPattern arguments %o', arguments);
        this.logger.debug('loadPattern url is', url);
        return this.get(url, this.token);
    }
    collectSources(data) {
        const locale = 'en_US';
        const url = this.vipConfig.host +
            `/${this.vipConfig.L10N_COMPONENT_API_ENDPOINT}` +
            `/products/${this.vipConfig.product}` +
            `/versions/${this.vipConfig.version}` +
            `/locales/${locale}` +
            `/components/${this.vipConfig.component}/keys`;
        return this.post(url, data);
    }
    collectSource(data) {
        const locale = 'en_US';
        const url = this.vipConfig.host +
            `/${this.vipConfig.L10N_COMPONENT_API_ENDPOINT}` +
            `/products/${this.vipConfig.product}` +
            `/versions/${this.vipConfig.version}` +
            `/locales/${locale}` +
            `/components/${this.vipConfig.component}` +
            `/keys/${key}`;
        return this.post(url, data);
    }
    get(url, token) {
        if (!token) {
            return axios.get(url);
        }

        return axios.get(url, {
            headers: { 'csp-auth-token': token }
        });
    }
    post(url, data, query) {
        query = query ? query : {
            collectSource: true,
            pseudo: false,
        }

        let headers = {
            'Content-Type': 'application/json'
        };

        if (this.token) {
            headers['csp-auth-token'] = this.token;
        }

        return axios.post(url, data, {
            params: query,
            headers
        });
    }
}
exports.VIPConfig = VIPConfig;
exports.VIPConfigLoadI18nPackage = VIPConfigLoadI18nPackage;
exports.VIPService = VIPService;