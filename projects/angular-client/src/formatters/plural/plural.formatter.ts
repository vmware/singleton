/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { PLURALFUNCS } from './plurals.func';
export class Plural {
    private _locale: string;
    constructor() { }
    private resolveLocale(locale: string): string {
        do {
            if (PLURALFUNCS[locale]) {
                return locale;
            } else if (PLURALFUNCS[locale.toLocaleLowerCase()]) {
                return locale.toLocaleLowerCase();
            }
            locale = locale.replace(/(-|_)?[^-_]*$/, '');
        } while (locale);
        return null;
    }
    getFunc(locale: string) {
        this._locale = this.resolveLocale(locale);
        return PLURALFUNCS[this._locale];
    }
}
