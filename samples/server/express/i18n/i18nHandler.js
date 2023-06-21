/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const singletonCore = require('@singleton-i18n/js-core-sdk-server');
const bundle = require('./source.l10n');


module.exports.handle = (req, res, next) => {
    // Current language and region could be fetched from cookie, query or HTTP header.
    let currentLanguage = 'zh-Hans';
    let currentRegion = 'CN'
    let i18nClient = singletonCore.i18nClient.createInstance(
        {
            productID: 'CoreSDK',
            version: '1.0.0',
            component: 'ui',
            host: 'http://localhost:8091',
            language: currentLanguage,
            region: currentRegion,
            sourceBundle: bundle.ENGLISH,
            i18nScope: [
                singletonCore.PatternCategories.DATE,
                singletonCore.PatternCategories.NUMBER,
                singletonCore.PatternCategories.PLURAL,
                singletonCore.PatternCategories.CURRENCIES,
            ],
            isPseudo: false,
            httpOptions: {
                timeout: 3000,
                withCredentials: true
            }
        }
    );
    // The method will attempt to fetch the data from a cache.
    // If no valid cache exists in the memory, this request will passed to Singleton service.
    i18nClient.coreService.loadI18nData(
        () => {
            req.t = (key, args) => {
                return i18nClient.l10nService.getMessage(key, args);
            };
            req.formatDate = (value, pattern) => {
                return i18nClient.i18nService.formatDate(value, pattern);
            };
            req.formatPercent = (value) => {
                return i18nClient.i18nService.formatPercent(value);
            };
            req.formatNumber = (value) => {
                return i18nClient.i18nService.formatNumber(value);
            };
            req.formatCurrency = (value, currencyCode) => {
                return i18nClient.i18nService.formatCurrency(value, currencyCode);
            };
            next();
        }
    )
}