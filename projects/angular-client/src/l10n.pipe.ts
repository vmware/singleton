/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

import { Pipe, PipeTransform, OnDestroy } from '@angular/core';
import { invalidParamater } from './exceptions';
import { L10nService } from './services/l10n.service';
import { isDefined, equals } from './util';
import { VIPConfig } from './config';

@Pipe({
    name: 'translate',
    pure: false
})

export class L10nPipe implements PipeTransform, OnDestroy {
    private value = '';
    private onTranslationChange: any;
    lastKey: string;
    lastParams: any;
    constructor(
        private l10nService: L10nService
    ) { }


    onTranslation(key: string, source: string, args: any[], locale: string) {
        this.value = this.l10nService.translate(key, source, args, locale);
        this.lastKey = key;
    }

    updateValue(key: string, source: string, args: string[], locale?: string) {
        if (locale) {
            this.onTranslation(key, source, args, locale);
        } else {
            this.l10nService.current.subscribe(
                (currentLocale: string) => {
                    this.onTranslation(key, source, args, currentLocale);
                }
            );
        }
    }


    transform(key: string, source: string, ...args: string[]): string {
        if (!isDefined(key)) {
            throw invalidParamater('key in L10nPipe');
        }

        if (equals(key, this.lastKey) && equals(args, this.lastParams)) {
            return this.value;
        }

        this.lastKey = key;
        this.lastParams = args;

        this.updateValue(key, source, args);
        this._dispose();

        if (!this.onTranslationChange) {
            this.onTranslationChange = this.l10nService.onLocaleChange.subscribe(
                (locale: string) => {
                    this.lastKey = null;
                    this.updateValue(key, source, args, locale);
                }
            );
        }
        return this.value;
    }

    private _dispose(): void {
        if (typeof this.onTranslationChange !== 'undefined') {
            this.onTranslationChange.unsubscribe();
            this.onTranslationChange = undefined;
        }
    }

    ngOnDestroy() {
        this._dispose();
    }
}

@Pipe({
    name: 'vtranslate',
    pure: false
})
export class L10nPipePlus implements PipeTransform, OnDestroy {
    private value = '';
    private onTranslationChange: any;
    config: VIPConfig;
    lastKey: string;
    lastParams: any;
    constructor(
        private l10nService: L10nService,
    ) { }

    onTranslation(key: string, args: any[], locale: string) {
        const longKey = this.l10nService.getLongKey(this.config, key);
        this.value = this.l10nService.getMessage(longKey, args, locale);
        this.lastKey = key;
    }

    updateValue(key: string, args: any[], locale?: string) {
        if (locale) {
            this.onTranslation(key, args, locale);
        } else {
            this.l10nService.current.subscribe(
                (currentLocale: string) => {
                    this.onTranslation(key, args, currentLocale);
                }
            );
        }
    }

    transform(key: string, ...args: any[]): string {
        if (!isDefined(key)) {
            throw invalidParamater('key in L10nPipePlus');
        }
        if (equals(key, this.lastKey) && equals(args, this.lastParams)) {
            return this.value;
        }

        this.lastKey = key;
        this.lastParams = args;

        this.updateValue(key, args);
        this._dispose();

        if (!this.onTranslationChange) {
            this.onTranslationChange = this.l10nService.onLocaleChange.subscribe(
                (locale: string) => {
                    this.lastKey = null;
                    this.updateValue(key, args, locale);
                }
            );
        }
        return this.value;
    }

    private _dispose(): void {
        if (typeof this.onTranslationChange !== 'undefined') {
            this.onTranslationChange.unsubscribe();
            this.onTranslationChange = undefined;
        }
    }

    ngOnDestroy() {
        this._dispose();
    }
}
