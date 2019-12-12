"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
var PatternCategories;
(function (PatternCategories) {
    PatternCategories["DATE"] = "dates";
    PatternCategories["NUMBER"] = "numbers";
    PatternCategories["PLURAL"] = "plurals";
    PatternCategories["CURRENCIES"] = "currencies";
})(PatternCategories = exports.PatternCategories || (exports.PatternCategories = {}));
function getDefaultConfig() {
    return {
        component: 'default',
        isPseudo: false,
        language: 'en',
        region: '',
        i18nScope: [],
        sourceBundle: {},
        i18nAssets: '',
        httpOptions: { timeout: 3000 }
    };
}
exports.getDefaultConfig = getDefaultConfig;
