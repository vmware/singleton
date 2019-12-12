"use strict";
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * verify value if undefined or null
 */
function isDefined(value) {
    return value !== undefined && value !== null && value !== '';
}
exports.isDefined = isDefined;
function resolveLanguageTag(languageTag) {
    if (!isDefined(languageTag)) {
        return languageTag;
    }
    return languageTag.split('_').join('-').toLocaleLowerCase();
}
exports.resolveLanguageTag = resolveLanguageTag;
/**
 * Returns the culture language code name from the browser, e.g. "de-DE"
 * @returns string
 */
function getBrowserCultureLang() {
    if (typeof window === 'undefined' || typeof window.navigator === 'undefined') {
        return undefined;
    }
    let browserCultureLang = window.navigator.languages ? window.navigator.languages[0] : null;
    browserCultureLang = browserCultureLang ||
        window.navigator.language ||
        window.navigator.browserLanguage ||
        window.navigator.userLanguage;
    return browserCultureLang;
}
exports.getBrowserCultureLang = getBrowserCultureLang;
/**
 * Merge the key-value pair which defined in each object and
 * check whether the key is globally unique
 * TODO: Support object directly
 * @param target Target object contains all strings
 * @param source A set of source objects
 */
function assign(target, source) {
    let s, i, props;
    const isObject = function (obj) {
        return obj && typeof obj === 'object';
    };
    if (!isObject(target)) {
        throw new TypeError('Target must be an object');
    }
    for (s = 0; s < source.length; ++s) {
        let sub;
        sub = source[s];
        if (!isObject(sub)) {
            throw new TypeError('Source must be an object');
        }
        props = Object.keys(Object(sub));
        for (i = 0; i < props.length; ++i) {
            if (target.hasOwnProperty(props[i])) {
                console.warn('Duplicate message key:', props[i]);
            }
            target[props[i]] = sub[props[i]];
        }
    }
    return target;
}
exports.assign = assign;
