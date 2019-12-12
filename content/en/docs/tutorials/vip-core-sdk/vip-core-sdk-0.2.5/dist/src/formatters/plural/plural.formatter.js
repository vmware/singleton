"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const plurals_func_1 = require("./plurals.func");
class Plural {
    constructor() { }
    resolveLocale(locale) {
        do {
            if (plurals_func_1.PLURALFUNCS[locale]) {
                return locale;
            }
            else if (plurals_func_1.PLURALFUNCS[locale.toLocaleLowerCase()]) {
                return locale.toLocaleLowerCase();
            }
            locale = locale.replace(/(-|)?[^-]*$/, '');
        } while (locale);
        return null;
    }
    getFunc(locale) {
        this._locale = this.resolveLocale(locale);
        return plurals_func_1.PLURALFUNCS[this._locale];
    }
}
exports.Plural = Plural;
