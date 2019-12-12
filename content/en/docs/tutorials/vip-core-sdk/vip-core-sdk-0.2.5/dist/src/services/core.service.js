"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const constants_1 = require("../constants");
const cache_1 = require("../cache");
const utils_1 = require("../utils");
const logger_1 = require("../logger");
const exceptions_1 = require("../exceptions");
const configuration_1 = require("../configuration");
class CoreService {
    constructor(coreLoader, resParser) {
        this.coreLoader = coreLoader;
        this.resParser = resParser;
        this.cacheManager = cache_1.CacheManager.createTranslationCacheManager();
        this.logger = logger_1.basedLogger.create('CoreService');
    }
    init(config) {
        const vipConfig = Object.assign({}, configuration_1.getDefaultConfig(), config);
        this.validateConfig(vipConfig);
        this.config = vipConfig;
        this.config.sourceBundle = this.resetSourceBundle();
        this.currentLanguage = vipConfig.language;
        this.currentRegion = vipConfig.region;
        this.httpOptions = vipConfig.httpOptions;
    }
    /**
     * Load resource prior to perform callback.
     * @param callback
     */
    loadI18nData(callback) {
        return __awaiter(this, void 0, void 0, function* () {
            const pendingList = [];
            let i18nResources = [];
            // callback should be performed always
            try {
                // for translations
                if (!this.isSourceLanguage(this.currentLanguage)) {
                    pendingList.push(this.loadTranslations(this.currentLanguage));
                }
                // for formatting patterns
                if (!this.isSourceLocale(this.currentLanguage, this.currentRegion)) {
                    pendingList.push(this.loadPatterns(this.currentRegion, this.currentLanguage));
                }
                i18nResources = yield (pendingList.length > 0 ? Promise.all(pendingList) : Promise.resolve([]));
            }
            catch (error) {
                this.logger.error('Load i18n resources failed.', error.message);
            }
            finally {
                if (callback && typeof callback === 'function') {
                    callback();
                }
            }
            return i18nResources;
        });
    }
    validateConfig(config) {
        if (!utils_1.isDefined(config.productID) || !config.productID.length) {
            throw exceptions_1.ParamaterError('CoreService', 'ProductID');
        }
        if (!utils_1.isDefined(config.version) || !config.version.length) {
            throw exceptions_1.ParamaterError('CoreService', 'Version');
        }
        if (!utils_1.isDefined(config.host) || !config.host.length) {
            throw exceptions_1.ParamaterError('CoreService', 'Host');
        }
        if (!utils_1.isDefined(config.language) || !config.language.length) {
            config.language = constants_1.Constants.SOURCE_LANGUAGE;
        }
    }
    getHost() {
        return this.config.host;
    }
    getProductID() {
        return this.config.productID;
    }
    getVersion() {
        return this.config.version;
    }
    getComponent() {
        return this.config.component;
    }
    getLanguage() {
        return this.currentLanguage;
    }
    getRegion() {
        return this.currentRegion;
    }
    getIsPseudo() {
        return this.config.isPseudo;
    }
    getI18nScope() {
        return this.config.i18nScope;
    }
    getSourceBundle() {
        return this.config.sourceBundle || undefined;
    }
    resetSourceBundle() {
        if (this.config.sourceBundle && Object.keys(this.config.sourceBundle).length > 0) {
            return this.config.sourceBundle;
        }
        if (this.config.sourceBundles) {
            if (Array.isArray(this.config.sourceBundles)) {
                return utils_1.assign({}, this.config.sourceBundles);
            }
        }
        return undefined;
    }
    /**
     * Identify the locale from specified language and region is source locale or not.
     * @param language
     * @param region
     */
    isSourceLocale(language, region) {
        return this.isSourceLanguage(language)
            && utils_1.isDefined(region)
            && region.toUpperCase() === constants_1.Constants.SOURCE_REGION ? true
            : this.isSourceLanguage(language) && !utils_1.isDefined(region) ? true
                : false;
    }
    /**
     * Identify specified language is source language or not.
     * @param language
     */
    isSourceLanguage(language) {
        return language.toLowerCase() === constants_1.Constants.SOURCE_LANGUAGE ? true
            : constants_1.Constants.SOURCE_LOCALE.toLowerCase() === utils_1.resolveLanguageTag(language) ? true
                : false;
    }
    getComponentTransUrl(language) {
        if (utils_1.isDefined(this.config.i18nAssets)) {
            const path = `${this.config.i18nAssets}${constants_1.Constants.L10N_ASSETS_PREFIX}${language}${constants_1.Constants.ASSETS_SUFFIX}`;
            return path;
        }
        return this.config.host
            .concat(constants_1.Constants.L10N_COMPONENT_API_ENDPOINT)
            .concat('/products/' + this.config.productID)
            .concat('/versions/' + this.config.version)
            .concat('/locales/' + language)
            .concat('/components/' + this.config.component)
            .concat('?pseudo=' + this.config.isPseudo);
    }
    /**
     * return promise with processed result.
     * @param language
     */
    loadTranslations(language) {
        const cache = this.cacheManager.lookforTranslationByComponent(this.getComponent(), language);
        if (typeof cache !== 'undefined') {
            return Promise.resolve(cache);
        }
        const componentTransUrl = this.getComponentTransUrl(language);
        const promise = this.coreLoader.getI18nResource(componentTransUrl, this.httpOptions);
        return promise.then((result) => {
            const translations = this.resParser.getTranslations(result);
            if (translations) {
                this.setTranslations(language, translations);
                return translations;
            }
        }).catch((err) => { this.logger.error('Load translations failed.', err.message); });
    }
    getPatternUrl(region, language) {
        if (utils_1.isDefined(this.config.i18nAssets)) {
            const path = `${this.config.i18nAssets}${constants_1.Constants.I18N_ASSETS_PREFIX}${region}${constants_1.Constants.ASSETS_SUFFIX}`;
            return path;
        }
        const categories = this.config.i18nScope.join(',');
        // When only set language, the language will be treated as a locale with default region.
        const endpoint = utils_1.isDefined(region)
            ? `?language=${language}&region=${region}`
            : `/locales/${language}`;
        let url = this.config.host
            .concat(constants_1.Constants.I18N_API_ENDPOINT)
            .concat(endpoint);
        if (categories) {
            const character = utils_1.isDefined(region) ? '&' : '?';
            url = url.concat(character + 'scope=' + categories);
        }
        return url;
    }
    loadPatterns(region, language) {
        const cache = this.cacheManager.lookforPattern(language, region);
        if (typeof cache !== 'undefined') {
            return Promise.resolve(cache);
        }
        if (!this.config.i18nScope || this.config.i18nScope.length < 1) {
            return;
        }
        const url = this.getPatternUrl(region, language);
        const getPatternPromise = this.coreLoader.getI18nResource(url, this.httpOptions);
        return getPatternPromise.then((result) => {
            const patterns = this.resParser.getPatterns(result);
            if (patterns) {
                this.setPatterns(patterns, language, region);
                return patterns;
            }
        }).catch((err) => { this.logger.error('Load formatting patterns failed.', err.message); });
    }
    getSupportedLanguagesUrl(displayLang) {
        let url = this.config.host
            .concat(constants_1.Constants.L10N_LOCAL_API_ENDPOINT)
            .concat('/supportedLanguageList?')
            .concat('productName=' + this.config.productID)
            .concat('&version=' + this.config.version);
        if (displayLang) {
            url = `${url}&displayLanguage=${displayLang}`;
        }
        return url;
    }
    getSupportedLanguages(displayLang) {
        const requestUrl = this.getSupportedLanguagesUrl(displayLang);
        const promise = this.coreLoader.getI18nResource(requestUrl, this.httpOptions).then((result) => {
            const languages = this.resParser.getSupportedLanguages(result);
            return languages;
        }).catch((err) => {
            this.logger.error(err.message);
        });
        return promise;
    }
    getSupportedRegionsUrl(language) {
        const url = this.config.host
            .concat(constants_1.Constants.L10N_LOCAL_API_ENDPOINT)
            .concat('/regionList?')
            .concat('supportedLanguageList=' + language);
        return url;
    }
    getSupportedRegions(language) {
        if (!utils_1.isDefined(language)) {
            throw exceptions_1.ParamaterError('CoreService', 'language');
        }
        const supportedRegionsUrl = this.getSupportedRegionsUrl(language);
        const promise = this.coreLoader.getI18nResource(supportedRegionsUrl, this.httpOptions).then((result) => {
            const regions = this.resParser.getSupportedRegions(result);
            return regions;
        }).catch((err) => {
            this.logger.error(err.message);
        });
        return promise;
    }
    setTranslations(language, translations) {
        if (!utils_1.isDefined(language)) {
            throw exceptions_1.ParamaterError('setTranslation', 'language');
        }
        if (!utils_1.isDefined(translations)) {
            return;
        }
        this.cacheManager.addTranslationByComponent(this.getComponent(), this.currentLanguage, translations);
    }
    setPatterns(patterns, language, region) {
        if (!utils_1.isDefined(language)) {
            throw exceptions_1.ParamaterError('setPattern', 'language');
        }
        if (!utils_1.isDefined(patterns)) {
            return;
        }
        this.cacheManager.addPatternByLocale(patterns, language, region);
    }
}
exports.CoreService = CoreService;
