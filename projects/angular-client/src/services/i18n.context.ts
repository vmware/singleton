/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injectable } from '@angular/core';

const I18N_ENABLED = 'vip.i18nEnabled';
const PSEUDO_ENABLED = 'vip.pseudoEnabled';
const PREFERRED_LANGUAGE = 'vip.preferredLanguage';
const PREFERRED_REGION = 'vip.preferredRegion';
import { isDefined } from '../util';

export abstract class I18nContext {
    abstract pseudoEnabled: boolean;
    abstract i18nEnabled: boolean;
    abstract preferredLanguage: string;
    abstract preferredRegion: string;
}

function supports_html5_storage() {
    try {
        return 'localStorage' in window && window['localStorage'] !== null;
    } catch (e) {
        return false;
    }
}

@Injectable()
export class DefaultI18nContext extends I18nContext {

    private store: any;
    private localstore: any = {};

    constructor() {
        super();
        if (supports_html5_storage) {
            this.store = window.localStorage;
        }
    }

    private toBoolean(value: string): boolean {
        if (value === undefined || value === 'undefined' ) {
            return undefined;
        }
        return value === 'true' ? true : false;
    }

    private setItem(item: string, value: string ) {
        this.localstore[item] = value;
        if (this.store) {
            this.store.setItem(item, value);
        }
    }

    private getItem(name: string): string {
        if ( this.store && isDefined(this.store.getItem(name)) ) {
            return this.store.getItem(name);
        }
        if ( isDefined(this.localstore[name]) ) {
            return this.localstore[name];
        }
    }

    get pseudoEnabled(): boolean {
        return this.toBoolean( this.getItem(PSEUDO_ENABLED) );
    }

    get i18nEnabled(): boolean {
        return this.toBoolean( this.getItem(I18N_ENABLED) );
    }

    set i18nEnabled(i18nEnabled: boolean) {
        this.setItem(I18N_ENABLED, String(i18nEnabled));
    }

    get preferredLanguage(): string {
        return this.getItem(PREFERRED_LANGUAGE);
    }

    set preferredLanguage(languageCode: string) {
        this.setItem(PREFERRED_LANGUAGE, languageCode);
    }

    get preferredRegion(): string {
        return this.getItem(PREFERRED_REGION);
    }

    set preferredRegion(regionCode: string) {
        this.setItem(PREFERRED_REGION, regionCode);
    }
}
