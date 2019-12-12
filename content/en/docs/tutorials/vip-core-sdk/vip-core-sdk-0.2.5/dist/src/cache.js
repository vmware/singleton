"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const utils_1 = require("./utils");
/**
 * Used to maintain the cache for translations and patterns.
 */
class Store {
}
exports.Store = Store;
var CacheNamespace;
(function (CacheNamespace) {
    CacheNamespace[CacheNamespace["L10N"] = 0] = "L10N";
    CacheNamespace[CacheNamespace["I18N"] = 1] = "I18N";
})(CacheNamespace || (CacheNamespace = {}));
class CacheManager {
    constructor() {
        this.cachedTranslationMap = new Map();
        this.cachedPatternsMap = new Map();
    }
    static createTranslationCacheManager() {
        if (!utils_1.isDefined(this.cacheManager)) {
            this.cacheManager = new CacheManager();
        }
        return this.cacheManager;
    }
    /**
     * generate unique cache key for translation object per component.
     * @param component
     * @param language
     */
    getTranslationCacheKey(component, language) {
        return [
            component,
            language,
            CacheNamespace.L10N
        ].join('_');
    }
    /**
     * generate unique cache key for pattern object per locale.
     * @param language
     * @param region
     */
    getPatternCacheKey(language, region) {
        return [
            utils_1.isDefined(region) ? [language, region].join('_') : language,
            CacheNamespace.I18N
        ].join('_');
    }
    /**
     * Get Component translation from cache.
     * @param cacheKey The key of cache.
     * @returns The translation map.
     */
    lookforTranslationByComponent(component, language) {
        const cacheKey = this.getTranslationCacheKey(component, language);
        if (!this.cachedTranslationMap.get(cacheKey)) {
            return undefined;
        }
        else {
            return this.cachedTranslationMap.get(cacheKey);
        }
    }
    /**
     * Get translation for specific key.
     * @param key
     * @param component
     * @param language
     */
    lookforTranslationByKey(key, component, language) {
        const cacheKey = this.getTranslationCacheKey(component, language);
        if (this.cachedTranslationMap.get(cacheKey) && this.cachedTranslationMap.get(cacheKey)[key]) {
            return this.cachedTranslationMap.get(cacheKey)[key];
        }
        return undefined;
    }
    /**
     * Add translations for the component.
     * @param component
     * @param language
     * @param msgObj
     */
    addTranslationByComponent(component, language, msgObj) {
        const cacheKey = this.getTranslationCacheKey(component, language);
        this.cachedTranslationMap.set(cacheKey, msgObj);
        return true;
    }
    /**
     * Add parttern to cache by cacheKey
     * @param cacheKey The key of cache.
     * @param patternsObj
     */
    addPatternByLocale(patternsObj, language, region) {
        const cacheKey = this.getPatternCacheKey(language, region);
        this.cachedPatternsMap.set(cacheKey, patternsObj);
        return true;
    }
    lookforPattern(language, region) {
        const cacheKey = this.getPatternCacheKey(language, region);
        if (this.cachedPatternsMap.get(cacheKey)) {
            return this.cachedPatternsMap.get(cacheKey);
        }
        return undefined;
    }
    /**
     * clean the cache.
     * @returns true or false
     */
    release() {
        this.cachedTranslationMap.clear();
        this.cachedPatternsMap.clear();
        return true;
    }
}
CacheManager.cacheManager = null;
exports.CacheManager = CacheManager;
