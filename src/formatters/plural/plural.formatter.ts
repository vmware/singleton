/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { PLURALFUNCS } from './plurals.func';
export class Plural {
    private _locale: string;
    constructor() { }
    private resolveLocale(locale: string): string {
        let lang: string = locale;
        do {      
            if (PLURALFUNCS[lang]) {
                return lang;
            } else if (PLURALFUNCS[lang.toLocaleLowerCase()]) {
                return lang.toLocaleLowerCase();
            }
            lang = locale.trim().replace(/-[a-zA-Z]+$/, '');
        } while (lang);
        return null;
    }
    getFunc(locale: string) {
        this._locale = this.resolveLocale(locale);
        return PLURALFUNCS[this._locale];
    }
}
