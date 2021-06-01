/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const superagent = require('superagent');

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
    get TRANSLATION_PATTERN() {
        return 'i18n/api/v2/combination/translationsAndPattern';
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
        return this.get(url);
    }
    loadSupportedRegions(displayName) {
        const url = this.vipConfig.host +
            `/${this.vipConfig.L10N_LOCAL_API_ENDPOINT}` +
            '/regionList?' +
            `supportedLanguageList=${displayName}`;
        this.logger.debug('loadSupportedRegions arguments %o', arguments);
        this.logger.debug('loadSupportedRegions url is ', url);
        return this.get(url);
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
        return this.get(url);
    }
    loadPattern(locale) {
        const url = this.vipConfig.host +
            `/${this.vipConfig.I18N_API_ENDPOINT}` +
            `/${locale}` +
            `?scope=${this.vipConfig.scope}`;
        this.logger.debug('loadPattern arguments %o', arguments);
        this.logger.debug('loadPattern url is', url);
        return this.get(url);
    }
    loadCombineData(locale) {
        const url = `${this.vipConfig.host}/${this.vipConfig.TRANSLATION_PATTERN}`;
        this.logger.debug(this.vipConfig.scope);
        const config = {
            language: locale,
            productName: this.vipConfig.product,
            version: this.vipConfig.version,
            components: [this.vipConfig.component],
            scope: this.vipConfig.scope,
            pseudo: false,
            combine: 2,
            machineTranslation: false
        }
        return this.post(url, config, {});
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
    get(url) {
        let that = this;
        if (!this.token) {
            return superagent.get(url).retry(3, function (err, res) {
                // node request will fall because the overload work of dns search, when this happen we retry it
                // think add throttle mechanism to remove this fix
                if (!err) {
                    return false;
                }
                that.logger.error('get request faild ', err);
                that.logger.info('get request retry');
                return true;
            });
        }
        return superagent.get(url).set('csp-auth-token', this.token);
    }
    post(url, data, query) {
        query = query ? query : {
            collectSource: true,
            pseudo: false,
        }
        var superPromise = superagent
            .post(url)
            .query(query)
            .send(data)
            .set('Content-Type', 'application/json');
        if (this.token) {
            superPromise.set('csp-auth-token', this.token);
        }
        return superPromise;
    }
}
exports.VIPConfig = VIPConfig;
exports.VIPConfigLoadI18nPackage = VIPConfigLoadI18nPackage;
exports.VIPService = VIPService;