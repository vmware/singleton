/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injectable } from '@angular/core';
import { DateFormatter } from '../formatters/date.formatter';
import { FormatterFactory } from '../formatters/number.formatter';
import { Plural } from '../formatters/plural/plural.formatter';
import { isDefined } from '../util';
import { LocaleService } from './locale.service';
import { I18nLoader } from '../loader';
import { DataForCurrency } from '../formatters/number.format.util';
import { PatternCategories } from '../config';
import { SOURCE_LOCALE_DATA } from '../data/locale_en';
import { BaseService } from './base.service';
import { VIPService, LocaleData } from './vip.service';


@Injectable()
export class I18nService extends BaseService {
    private sourceLocaleData: any;
    private plural: Plural;
    private formatter: FormatterFactory;

    constructor(
        private dateFormatter: DateFormatter,
        protected localeService: LocaleService,
        protected vipService: VIPService,
        public currentLoader: I18nLoader
    ) {
        super(vipService, localeService);
        this.initSourcePatterns();
        this.formatter = new FormatterFactory();
        this.plural = new Plural();
    }

    public resolveLocaleData(locale: string) {
        let patterns: any;
        let localeData: LocaleData;
        const currentLocale = locale ? locale : this.localeService.getCurrentLocale();
        localeData = this.vipService.localeData[currentLocale];
        patterns = localeData && localeData.categories ? localeData.categories : undefined;
        return patterns;
    }

    private initSourcePatterns() {
        this.sourceLocaleData = SOURCE_LOCALE_DATA;
        this.sourceLocaleData.categories[PatternCategories.CURRENCIES] = this.getCurrencyData(this.sourceLocaleData.categories);
    }

    private validateNumber(value: number | string): number {
        let number: any;
        if (typeof value === 'string' && !isNaN(+value - parseFloat(value))) {
            number = +value;
        } else if (typeof value !== 'number') {
            throw new Error(`'${value}' is not a number`);
        } else {
            number = value;
        }
        return number;
    }

    public getFormattedDateTime(value: any, pattern: string, locale: string, timezone?: string, ): any {
        const date = this.dateFormatter.getStandardTime(value);
        const type = Object.prototype.toString.call(date);
        if (!(type === '[object Date]') || !isFinite(date.getTime())) {
            throw new Error(`InvalidPipeArgument: '${date}' for pipe 'dateFormat'`);
        }
        const dataForDate = this.getPattern(PatternCategories.DATE, locale) || this.getSourcePattern(PatternCategories.DATE);
        return this.dateFormatter.getformattedString(date, pattern, dataForDate, '-', timezone);
    }

    public getFormattedDecimal(value: any, locale: string): string {
        const dataForNumber = this.getPattern(PatternCategories.NUMBER, locale) || this.getSourcePattern(PatternCategories.NUMBER);
        value = this.validateNumber(value);
        const formatter = this.formatter.decimal(dataForNumber, locale);
        const text = formatter(value);
        return text;
    }

    public getFormattedPercent(value: any, locale: string): string {
        const dataForNumber = this.getPattern(PatternCategories.NUMBER, locale) || this.getSourcePattern(PatternCategories.NUMBER);
        value = this.validateNumber(value);
        const formatter = this.formatter.percent(dataForNumber, locale);
        const text = formatter(value);
        return text;
    }

    public getFormattedCurrency(value: any, currencyCode: string, locale: string): any {
        value = this.validateNumber(value);
        const dataForCurrency = this.getPattern(PatternCategories.CURRENCIES, locale)
            || this.getSourcePattern(PatternCategories.CURRENCIES);
        const formatter = this.formatter.currencies(dataForCurrency, locale);
        const text = formatter(value, currencyCode);
        return text;
    }

    /**
     * This APIs will be call by l10n service when render plural message
     * @param value Numerals that need to be dealt with
     * @param locale Locale  from l10n service
     */
    public getPluralCategoryType(value: number, locale: string): string | undefined {
        value = this.validateNumber(value);
        value = Math.abs(value);
        // try to round number with default number formatting rules
        // if data isn't exist, use origin number.
        // getPattern will fallback to sourceLocale. Use validateScope to validate is correct formatting data exist.
        try {
            const dataForNumber = this.getPattern(PatternCategories.NUMBER, locale);
            const formatter = this.formatter.roundNumberForPlural(dataForNumber, locale);
            value = Number(formatter(value));
        } catch (error) { }
        const pluraFunction = this.getPluralFunction(locale);
        const type = pluraFunction ? pluraFunction(value) : undefined;
        return type;
    }

    private getPluralFunction(locale: string) {
        return this.plural.getFunc(locale);
    }

    private validateScope(type: PatternCategories): boolean {
        if (!this.localeService.isSourceLocale && this.vipService.i18nScope.indexOf(type) === -1) {
            throw new Error(`You should add '${type}' to 'i18nScope' in initialize configuration`);
        }
        return true;
    }

    private getSourcePattern(type: PatternCategories) {
        return this.sourceLocaleData.categories[type];
    }

    private getPattern(type: PatternCategories, locale: string): any {
        if (this.localeService.shouldSourceLocale(locale)) {
            return this.getSourcePattern(type);
        }
        this.validateScope(type);
        const localeData = this.resolveLocaleData(locale);
        if (localeData && type === PatternCategories.CURRENCIES) {
            // number formatting is part of currency format.
            localeData[PatternCategories.CURRENCIES] = this.getCurrencyData(localeData);
        }
        return localeData && localeData[type];
    }

    private getCurrencyData(data: any): DataForCurrency | undefined {
        // TODO: data sharing.  Avoid duplicate processing.
        if (data[PatternCategories.CURRENCIES].currencyFormats) {
            return data[PatternCategories.CURRENCIES];
        }
        return data[PatternCategories.NUMBER] ? {
            currencyFormats: data[PatternCategories.NUMBER].numberFormats.currencyFormats,
            numberSymbols: data[PatternCategories.NUMBER].numberSymbols,
            currencySymbols: data[PatternCategories.CURRENCIES],
            fractions: data['supplemental'].currencies.fractions
        } : undefined;
    }

    async getSupportedLanguages(): Promise<any> {
        if (!this.vipService.mainConfig) {
            console.error('No main component to get supported  languages');
            return Promise.resolve(undefined);
        }
        let supportedLanguages: Object | undefined =
            await this.currentLoader
                .getSupportedLanguages(this.vipService.mainConfig)
                .toPromise()
                .then(
                    (res: any) => {
                        if (isDefined(res)) {
                            return res;
                        }
                    }
                ).catch((error: any) => { console.error('Failed to get supported languages from VIP service.', error); });
        supportedLanguages = supportedLanguages ? supportedLanguages :
            [{
                languageTag: this.localeService.defaultLocale.languageCode,
                displayName: this.localeService.defaultLocale.languageName
            }];
        return supportedLanguages;
    }

    async getSupportedRegions(language: string): Promise<any> {
        if (!this.vipService.mainConfig) {
            console.error('No main component to get supported regions');
            return Promise.resolve(undefined);
        }
        let supportedRegions: Object | undefined = await this.currentLoader
            .getSupportedRegions(language || this.localeService.getCurrentLanguage(), this.vipService.mainConfig)
            .toPromise()
            .catch((error: any) => { console.error('Failed to get supported regions from VIP service.', error); });
        const defaultRegion = [[this.localeService.defaultLocale.regionCode,
        this.localeService.defaultLocale.regionName]];
        if (supportedRegions) {
            supportedRegions = this.convertObjectToArray(supportedRegions);
        }
        supportedRegions = supportedRegions ? supportedRegions : defaultRegion;
        return supportedRegions;
    }

    private convertObjectToArray(obj: any): any {
        if (!obj || !obj.territories) {
            return undefined;
        }
        const defaultRegion = obj.defaultRegionCode;
        const territories = obj.territories;
        if (!obj) { return []; }
        const result: any[] = [];
        for (const key in territories) {
            if (key) {
                const item = [key, territories[key]]; // convert region object to array
                if (key === defaultRegion) { // put default region at top of the dropdown list
                    result.unshift(item);
                } else {
                    result.push(item);
                }
            }
        }
        return result;
    }
}
