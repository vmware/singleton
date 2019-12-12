"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const index_1 = require("../index");
describe('cacheManager', () => {
    const cacheManager = index_1.CacheManager.createTranslationCacheManager();
    const cacheObj = { 'name': 'test' }, cachePattern = { patterns: {} };
    it('should create cacheManager', () => {
        expect(cacheManager instanceof index_1.CacheManager);
        expect(cacheManager instanceof index_1.Store);
    });
    it('should add cache by component', () => {
        cacheManager.addTranslationByComponent('component', 'en', cacheObj);
        cacheManager.addPatternByLocale(cachePattern, 'en', 'US');
        cacheManager.addPatternByLocale(cachePattern, 'de');
        expect(cacheManager.lookforTranslationByComponent('component', 'en')).toEqual(cacheObj);
        expect(cacheManager.lookforPattern('en', 'US')).toEqual(cachePattern);
        expect(cacheManager.lookforPattern('de')).toEqual(cachePattern);
    });
    it('look for translation in by component', () => {
        expect(cacheManager.lookforTranslationByComponent('component', 'en')).toEqual(cacheObj);
        expect(cacheManager.lookforTranslationByComponent('component1', 'en')).toEqual(undefined);
    });
    it('look for translation in cache', () => {
        expect(cacheManager.lookforTranslationByKey('name', 'component', 'en')).toEqual('test');
    });
    it('release cache', () => {
        expect(cacheManager.release()).toBeTruthy();
        expect(cacheManager.lookforTranslationByKey('name', 'component', 'en')).toBeUndefined();
        expect(cacheManager.lookforPattern('en', 'US')).toBeUndefined();
    });
    afterAll(() => {
        cacheManager.release();
    });
});
