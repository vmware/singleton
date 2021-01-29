/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

import { Directive, ElementRef, Input, AfterViewInit, OnDestroy } from '@angular/core';
import { L10nService } from './services/l10n.service';
import { isDefined } from './util';
import { invalidParamater } from './exceptions';


@Directive({
    selector: '[l10n]'
})
export class L10nDirective implements AfterViewInit, OnDestroy {

    @Input('l10n') l10n: string;
    @Input() source: string;
    @Input() comment: string;
    @Input() params: string[];
    private onTranslationChange: any;

    constructor(private el: ElementRef,
        private l10nService: L10nService) { }

    ngAfterViewInit() {
        this.getMessage();
    }

    updateValue(key: string, source: string, args: string[], locale?: string) {
        const onTranslation = (currentLocale: string) => {
            const translation = this.l10nService.translate(key, source, args, currentLocale);
            this.el.nativeElement.textContent = translation;
        };
        if (locale) {
            onTranslation(locale);
        } else {
            this.l10nService.current.subscribe(onTranslation);
        }
    }

    getMessage() {
        if (!isDefined(this.l10n)) {
            throw invalidParamater('key in L10nDirective.');
        }
        const source = isDefined(this.source) ? this.source : this.l10nService.getSourceString(this.l10n);
        if (!this.onTranslationChange) {
            this.onTranslationChange = this.l10nService.onLocaleChange.subscribe(
                (locale: string) => {
                    this.updateValue(this.l10n, source, this.params, locale);
                }
            );
        }
        this.updateValue(this.l10n, source, this.params);
    }

    ngOnDestroy() {
        if (typeof this.onTranslationChange !== 'undefined') {
            this.onTranslationChange.unsubscribe();
            this.onTranslationChange = undefined;
        }
    }
}
