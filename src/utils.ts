/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

/**
 * verify value if undefined or null
 */
export function isDefined(value: any) {
    return value !== undefined && value !== null && value !== '';
}

export function resolveLanguageTag(languageTag: string) {
    if (!isDefined(languageTag)) {
        return languageTag;
    }
    return languageTag.split('_').join('-').toLocaleLowerCase();
}

declare interface Window {
    navigator: any;
}
declare const window: Window;
/**
 * Returns the culture language code name from the browser, e.g. "de-DE"
 * @returns string
 */
export function getBrowserCultureLang(): string {
    if (typeof window === 'undefined' || typeof window.navigator === 'undefined') {
        return undefined;
    }

    let browserCultureLang: any = window.navigator.languages ? window.navigator.languages[0] : null;
    browserCultureLang = browserCultureLang ||
        window.navigator.language ||
        window.navigator.browserLanguage ||
        window.navigator.userLanguage;
    return browserCultureLang;
}

/**
 * Merge the key-value pair which defined in each object and
 * check whether the key is globally unique
 * TODO: Support object directly
 * @param target Target object contains all strings
 * @param source A set of source objects
 */
export function assign(target: { [x: string]: any; }, source: { [x: string]: any; }[]) {
    let s: number, i: number, props: string[];
    const isObject = function (obj: any) {
        return obj && typeof obj === 'object';
    };
    if (!isObject(target)) {
        throw new TypeError('Target must be an object');
    }
    for (s = 0; s < source.length; ++s) {
        let sub: { [x: string]: any; };
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
