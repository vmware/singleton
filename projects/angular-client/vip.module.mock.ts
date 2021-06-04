/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { ModuleWithProviders, NgModule, Pipe, PipeTransform, Injectable } from '@angular/core';
import { Directive, ElementRef, Input, AfterViewInit } from '@angular/core';
import { Observable, Subject, BehaviorSubject, of } from 'rxjs';

import { DateFormatter } from './src/formatters/date.formatter';
import { L10nService } from './src/services/l10n.service';
import { I18nService } from './src/services/i18n.service';
import { VIPService } from './src/services/vip.service';
import { PatternCategories, VIPConfig, getNameSpace } from './src/config';
import { VIPServiceConstants } from './src/constants';
import { isDefined } from './src/util';

@Pipe({ name: 'translate' })
export class MockL10nPipe implements PipeTransform {
    transform(key: string, value: string): string {
        const args = [];
        for (let i = 2; i < arguments.length; i++) {
            args[i - 2] = arguments[i];
        }
        return L10nFormat(value, args);
    }
}

@Pipe({ name: 'vtranslate' })
export class MockL10nPipePlus implements PipeTransform {
    constructor(private l10nService: L10nService) { }
    transform(key: string, ...args: string[]): string {
        if (typeof key !== 'string') { return undefined; }
        if (!isDefined(key)) {
            return null;
        }
        const source = this.l10nService.getSourceString(key);
        // If source is undefined return key. Avoid error due to source parsing in L10nFormat.
        if (!source) { return key; }
        return L10nFormat(source, args);
    }
}

@Pipe({ name: 'currencyFormat' })
export class MockCurrencyPipe implements PipeTransform {
    transform(value: string, currency: string): string {
        return {
            'USD': '$',
        }[currency] + value;
    }
}

@Pipe({ name: 'dateFormat' })
export class MockDatePipe implements PipeTransform {
    transform(value: string, format: string): string {
        const date = new Date(value);
        return {
            'medium': `${date.getUTCMonth()}/${date.getUTCDate()}/${date.getUTCFullYear()}`,
            'short': `${date.getUTCMonth()}/${date.getUTCDate()}/${date.getUTCFullYear()}`,
        }[format] || value;
    }
}

@Pipe({ name: 'numberFormat' })
export class MockNumberFormatPipe implements PipeTransform {
    transform(value: any, locale?: string): string {
        return value + '';
    }
}

@Directive({
    selector: '[l10n]'
})
export class MockL10nDirective implements AfterViewInit {

    @Input('l10n') l10n: string;
    @Input() source: string;
    @Input() comment: string;
    @Input() params: string[];
    constructor(private el: ElementRef, private l10nService: L10nService) { }

    ngAfterViewInit() {
        if (!isDefined(this.l10n)) {
            throw Error('key error');
        }
        const source = isDefined(this.source) ? this.source : this.l10nService.getSourceString(this.l10n);
        // If source is undefined return key. Avoid error due to source parsing in L10nFormat.
        this.el.nativeElement.textContent = source ? L10nFormat(source, this.params) : this.l10n;
    }

}


export class TranslationLoader {
    getTranslationByComponent(url: string): Promise<any> {
        return Promise.resolve('mock');
    }
}

@Injectable()
export class VIPServiceMock {
    private translationLoader: TranslationLoader;
    public mainConfig: any = {};

    constructor(translationLoader: TranslationLoader) {
        this.translationLoader = translationLoader;
    }

    initData(initData: VIPConfig): Promise<any> {
        this.mainConfig = initData;
        // there's really no need to ever call this
        return Promise.resolve('mock');
    }

    registerComponent(config: VIPConfig, isMain: boolean = false) {
        return;
    }

    getLocale(): string {
        return 'en_US';
    }

    localeData() {
        return {};
    }

    getI18nScope(): PatternCategories[] {
        return [
            PatternCategories.CURRENCIES,
            PatternCategories.DATE,
            PatternCategories.NUMBER,
            PatternCategories.PLURAL,
        ];
    }
}

export class BaseDataServiceMock {
    coreService = {
        onTranslationChange: new Subject(),
    };
    currentLocale = 'en-US';

    current = new BehaviorSubject(this.currentLocale);
    stream = new BehaviorSubject(this.currentLocale);
    onLocaleChange = new BehaviorSubject(this.currentLocale);
}

type sourceBundleObject = { [key: string]: any };

export class L10nServiceMock extends BaseDataServiceMock {
    constructor() {
        super();
    }

    getLongKey(config: VIPConfig, key: string) {
        const nameSpace = config ? getNameSpace(config) : undefined;
        return nameSpace ? nameSpace
            .concat(VIPServiceConstants.NAME_SPACE_SEPARATOR)
            .concat(key) : key;
    }

    translate(key: string, value: string, args?: any[], comment?: string): string {
        return L10nFormat(value, args);
    }

    getTranslation(key: string, source: string, args?: any[], comment?: string): Observable<string> {
        return of(this.translate(key, source, args));
    }

    getMessage(key: string, args?: any[]): string {
        if (typeof key !== 'string') { return undefined; }
        if (!isDefined(key)) {
            return null;
        }
        const source = this.getSourceString(key);
        // If source is undefined return key. Avoid error due to source parsing in L10nFormat.
        if (!source) { return key; }
        const translation = this.translate(key, source, args);
        return translation;
    }

    // Todo: add method to register sourceBundle under certain namespace.
    getSourceString(key: string): string {
        return undefined;
    }

    registerSourceBundles(...args: sourceBundleObject[]): void { }
}

export class I18nServiceMock extends BaseDataServiceMock {
    constructor() {
        super();
    }

    public formatCurrency(value: any, currencyCode: string): any {
        return {
            'USD': '$',
        }[currencyCode] + value;
    }

    public formatDate(value: any, pattern: string, timezone?: string): any {
        return value;
    }

    public formatNumber(value: any, locale?: string): string {
        return value + '';
    }

    public formatPercent(value: any, locale?: string): string {
        return value;
    }

    public getLocalizedPattern(pattern: string, locale?: string): string {
        return {
            'shortTime': 'h:mm a',
            'mediumTime': 'h:mm:ss a',
            'longTime': 'h:mm:ss z',
            'fullTime': 'h:mm:ss zzzz',
            'shortDate': 'M/d/yy',
            'mediumDate': 'MMM d, y',
            'longDate': 'MMMM d, y',
            'fullDate': 'EEEE, MMMM d, y',
            'short': 'M/d/yy, h:mm a',
            'medium': 'MMM d, y, h:mm:ss a',
            'long': 'MMMM d, y, h:mm:ss a z',
            'full': 'EEEE, MMMM d, y, h:mm:ss a zzzz',
        }[pattern];
    }
}

@NgModule({
    imports: [],
    declarations: [
        MockL10nPipe,
        MockL10nPipePlus,
        MockL10nDirective,
        MockCurrencyPipe,
        MockDatePipe,
        MockNumberFormatPipe,
    ],
    exports: [
        MockL10nPipe,
        MockL10nPipePlus,
        MockL10nDirective,
        MockCurrencyPipe,
        MockDatePipe,
        MockNumberFormatPipe,
    ]
})
export class VIPModuleMock {
    /**
     * Use this method in your root module to provide the mocked VIP Service
     * @static
     * @returns {ModuleWithProviders}
     * @memberof VIPModule
     */
    static forRoot(): ModuleWithProviders<VIPModuleMock> {
        return {
            ngModule: VIPModuleMock,
            providers: [
                TranslationLoader,
                DateFormatter,
                { provide: VIPService, useClass: VIPServiceMock },
                { provide: L10nService, useClass: L10nServiceMock },
                { provide: I18nService, useClass: I18nServiceMock },
            ]
        };
    }
}

export function L10nFormat(message: string, args: any[]) {
    if (message.indexOf('=0')>-1 || message.indexOf('=1')>-1) {
        let substr, wordNumber: string;
        switch (args[0]) {
            case 0:
                substr = '=0';
                wordNumber = "";
                break;
            case 1:
                substr = '=1';
                wordNumber = "one";
                break;
            default:
                substr = 'other';
                wordNumber = args[0].toString();
                break;
        }

        const startSubstr = message.indexOf(substr) + substr.length + 1;
        return message.substring(startSubstr, message.indexOf('}', startSubstr)).replace('#', wordNumber);
    }

    const templateMatcher = /{\s?([\d]*)\s?}/g;

    if (args && args.length) {
        const strings = [].concat(...args);   // args could be an array of arrays, or just an array so flatten
        for (let i = 0; i < strings.length; i++) {
            message = message.replace(templateMatcher, function (substring: string, b: string) {
                return (b.trim() === i.toString() && strings[i] != null) ? strings[i] : substring;
            });
        }
    }
    return message.trim();
}
