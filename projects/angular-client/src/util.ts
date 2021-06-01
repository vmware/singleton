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

/**
 * Format translation string, e.g. 'No custom buckets specified for {0}'
 */
export function format(message: string, args: any[]) {
    const templateMatcher: RegExp = /{\s?([\d]*)\s?}/g;
    for (let i = 0; i < args.length; i += 1) {
        message = message.replace(templateMatcher, (substring: string, b: string) => {
            return (b.trim() === i.toString() && isDefined(args[i])) ? args[i] : substring;
        });
    }
    return message;
}

export function mergeObject(a: any, b: any): Object {
    a = a instanceof Object ? a : {};
    b = b instanceof Object ? b : {};

    // Merge the sub objects with the same key.
    Object.keys(a).forEach(
        (key) => {
            if (b[key]) {
                if ((b[key] instanceof Object) && (a[key] instanceof Object)) {
                    Object.assign(b[key], a[key]);
                }
            }
        });
    return Object.assign(a, b);
}

export function equals(o1: any, o2: any): boolean {
    if (o1 === o2) { return true; }
    if (o1 === null || o2 === null) { return false; }
    if (o1 !== o1 && o2 !== o2) { return true; } // NaN === NaN
    const t1 = typeof o1, t2 = typeof o2;
    let length: number, key: any, keySet: any;
    if (t1 === t2 && t1 === 'object') {
        if (Array.isArray(o1)) {
            if (!Array.isArray(o2)) { return false; }
            if ((length = o1.length) === o2.length) {
                for (key = 0; key < length; key++) {
                    if (!equals(o1[key], o2[key])) { return false; }
                }
                return true;
            }
        } else {
            if (Array.isArray(o2)) {
                return false;
            }
            // Dealing with the Date object without any key, but the actual value is different.
            if ( o1 instanceof Date && o2 instanceof Date ) {
                return o1.getTime() === o2.getTime();
            }
            keySet = Object.create(null);
            for (key in o1) {
                if (!equals(o1[key], o2[key])) {
                    return false;
                } else {
                    keySet[key] = true;
                }
            }
            for (key in o2) {
                if (!(key in keySet) && typeof o2[key] !== 'undefined') {
                    return false;
                }
            }
            return true;
        }
    }
    return false;
}
/*
 * Mixin class decorator
 *
 * Based on
 * https://medium.com/@dmyl/mixins-as-class-decorators-in-typescript-angular2-8e09f1bc1f02
 *
 * with minor modifications to support an opaque data object
 * stored in the object prototype as `mixinData`
 */
export function Mixin(baseCtors: Function[], data?: any) {
    return function (derivedCtor: Function) {
        derivedCtor.prototype.mixinData = data;
        baseCtors.forEach(baseCtor => {
            Object.getOwnPropertyNames(baseCtor.prototype).forEach(name => {
                const descriptor = Object.getOwnPropertyDescriptor(baseCtor.prototype, name);

                if (name === 'constructor') {
                    return;
                }

                if (descriptor && (!descriptor.writable || !descriptor.configurable
                    || !descriptor.enumerable || descriptor.get || descriptor.set)) {
                    Object.defineProperty(derivedCtor.prototype, name, descriptor);
                } else {
                    derivedCtor.prototype[name] = baseCtor.prototype[name];
                }

            });
        });
    };
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
    if (window === undefined || window.navigator === undefined) {
        return undefined;
    }

    let browserCultureLang: any = window.navigator.languages ? window.navigator.languages[0] : null;
    browserCultureLang = browserCultureLang ||
        window.navigator.language ||
        window.navigator.browserLanguage ||
        window.navigator.userLanguage;
    return browserCultureLang;
}

export function deprecatedWarn(name: string, version: string, substitute: string) {
    console.warn(`${name} is deprecated, will be removed in ${version}, Please use ${substitute} instead.`);
}

/**
 * Merge the key-value pair which defined in each object and
 * check whether the key is globally unique
 * TODO: Support object directly
 * @param target Target object contains all strings
 * @param source A set of source objects
 */
export function assign(target: { [x: string]: any; }, source: { [x: string]: any; }[]) {
    let s: number;
    let i: number;
    let props: string[];
    const isObject = (obj: any) => {
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

export function isEmptyObject(obj: any) {
    if( !isDefined(obj) ) { return true; }
    return Object.keys(obj).length === 0;
}

export function parseOption(name: string, range: number[], a: any) {
    const res  = parseInt(a);
    if (isNaN(res)) {
        throw Error(`${name} is not a number`);
    }

    if ( res < range[0] || res > range[1]) {
        throw Error( `${name} value is out of range.`)
    }
    return res;
}