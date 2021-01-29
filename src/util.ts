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
          derivedCtor.prototype.mixinData = data;
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
