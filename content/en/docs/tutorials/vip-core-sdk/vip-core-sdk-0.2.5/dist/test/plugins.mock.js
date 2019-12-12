"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const index_1 = require("../index");
exports.mockResponse = {
    trans: {
        test: 'testing string'
    },
    pattern: {
        categories: {
            date: 'test'
        }
    },
    languages: [
        {
            languageCode: 'zh-CN',
            displayName: 'Simplified Chinese'
        }
    ],
    regions: {
        territories: [
            {
                AC: 'Ascension Island',
                DE: 'Germany'
            }
        ]
    }
};
class CustomLoader extends index_1.Loader {
    getI18nResource(url, options) {
        if (url.indexOf('/components/') !== -1) {
            return new Promise((resolve) => {
                resolve(exports.mockResponse.trans);
            });
        }
        else if (url.indexOf('/patterns/locales') !== -1) {
            return new Promise((resolve) => {
                resolve(exports.mockResponse.pattern);
            });
        }
        else if (url.indexOf('supportedLanguageList?') !== -1) {
            return new Promise((resolve) => {
                resolve(exports.mockResponse.languages);
            });
        }
        else if (url.indexOf('/regionList?') !== -1) {
            return new Promise((resolve) => {
                resolve(exports.mockResponse.regions);
            });
        }
    }
}
exports.CustomLoader = CustomLoader;
class CustomParser extends index_1.ResponseParser {
    validateResponse(res) {
        return res;
    }
    getPatterns(res) {
        const data = this.validateResponse(res);
        const pattern = data && data.categories ? data.categories : null;
        return pattern;
    }
    getTranslations(res) {
        const data = this.validateResponse(res);
        const translations = data ? data : null;
        return translations;
    }
    getSupportedLanguages(res) {
        const data = this.validateResponse(res);
        const languages = data ? data : null;
        return languages;
    }
    getSupportedRegions(res) {
        const data = this.validateResponse(res);
        const regions = data ? data : null;
        return regions;
    }
}
exports.CustomParser = CustomParser;
