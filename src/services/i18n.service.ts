/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { DateFormatter } from '../formatters/date.formatter';
import { FormatterFactory } from '../formatters/number.formatter';
import { Plural } from '../formatters/plural/plural.formatter';
import { CoreService } from './core.service';
import { Store } from '../cache';
import { PatternCategories } from '../configuration';
import { DataForCurrency } from '../formatters/number.format.util';
import sourcePatternData from '../data/locale_en';
import { isDefined } from '../utils';
import { invalidParamater } from '../exceptions';


export class I18nService {
    private sourceI18nData: any;
    private formatter: FormatterFactory;
    private plural: Plural;

    constructor(
        private coreService: CoreService,
        private dateFormatter: DateFormatter,
        private cacheManager: Store,
    ) {
        this.formatter = new FormatterFactory();
        this.plural = new Plural();
        // init source pattern data.
        this.sourceI18nData = sourcePatternData;
        this.sourceI18nData.categories[PatternCategories.CURRENCIES] = this.getCurrencyData(this.sourceI18nData.categories);
    }

    private validateScope(type: PatternCategories): boolean {
        if (this.coreService.getI18nScope().indexOf(type) === -1) {
            throw new Error(`You should add '${type}' to 'i18nScope' in initialize configuration`);
        }
        return true;
    }

    private getPattern(type: PatternCategories, locale?: string): any {
        const language = this.coreService.getLanguage(),
            region = this.coreService.getRegion();
        const isSourceLocale = locale && this.coreService.isSourceLocale(locale);
        if ( isSourceLocale ||  this.coreService.isSourceLocale(language, region)) {
            return this.getSourcePattern( type );
        }
        this.validateScope( type );
        const localeData = this.cacheManager.lookforPattern(this.coreService.getLanguage(),
            this.coreService.getRegion());
        if (localeData && type === PatternCategories.CURRENCIES) {
            // number formatting is part of currency format.
            localeData[PatternCategories.CURRENCIES] = this.getCurrencyData(localeData);
        }
        let data;
        if (localeData && localeData[type]) {
            data = this.isEmptyObject(localeData[type]) ? undefined : localeData[type];
        }
        return data;
    }

    private isEmptyObject( obj: any ) {
        if (!isDefined(obj)) {
            return true;
        }
        const keys = Object.keys(obj);
        return keys.length ? false : true;
    }

    private getSourcePattern( type: PatternCategories ) {
        return this.sourceI18nData.categories[type];
    }

    private getCurrencyData(data: any): DataForCurrency | null {
        if (this.isEmptyObject(data) || this.isEmptyObject(data[PatternCategories.CURRENCIES])) {
            return undefined;
        }
        // TODO: data sharing.  Avoid duplicate processing.
        if (data[PatternCategories.CURRENCIES].currencyFormats) {
            return data[PatternCategories.CURRENCIES];
        }
        const isNumberPatternExist = data[PatternCategories.NUMBER] && !this.isEmptyObject(data[PatternCategories.NUMBER]);
        return  isNumberPatternExist ? {
            currencyFormats: data[PatternCategories.NUMBER].numberFormats.currencyFormats,
            numberSymbols: data[PatternCategories.NUMBER].numberSymbols,
            currencySymbols: data[PatternCategories.CURRENCIES],
            fractions: data['supplemental'].currencies.fractions
        } : null;
    }

    private validateNumber(value: number | string, type: string): number {
        if (!this.validateInput(value)) {
            throw invalidParamater(`Invalid number '${value}' for '${type}'`);
        }
        let number;
        if (typeof value === 'string' && !isNaN(+value - parseFloat(value))) {
            number = +value;
        } else if (typeof value !== 'number') {
            throw invalidParamater(`Invalid number '${value}' for '${type}'`);
        } else {
            number = value;
        }
        return number;
    }

    private validateInput( value: any ) {
        return isDefined( value ) && value === value;
    }

    public formatDate(value: any, pattern: string = 'mediumDate', timezone?: string): any {
        if ( !this.validateInput(value) ) {
            throw invalidParamater(`Invalid date '${value}' for 'formatDate'`);
        }
        const date = this.dateFormatter.getStandardTime(value);
        const type = Object.prototype.toString.call(date);
        if ((type !== '[object Date]') || !isFinite(date.getTime())) {
            throw invalidParamater(`Invalid date '${value}' for 'formatDate'`);
        }
        const dataForDate = this.getPattern(PatternCategories.DATE) || this.getSourcePattern(PatternCategories.DATE);
        return this.dateFormatter.getformattedString(date, pattern, dataForDate, '-', timezone);
    }

    public formatNumber(value: any, locale?: string): string {
        const dataForNumber = this.getPattern(PatternCategories.NUMBER, locale) || this.getSourcePattern(PatternCategories.NUMBER);
        value = this.validateNumber(value, 'formatNumber');
        const formatter = this.formatter.decimal(dataForNumber, locale);
        const text = formatter(value);
        return text;
    }

    public formatPercent(value: any): string {
        const dataForNumber = this.getPattern(PatternCategories.NUMBER)  || this.getSourcePattern(PatternCategories.NUMBER);
        value = this.validateNumber(value, 'formatPercent');
        const locale = this.coreService.getLanguage() + '-' + this.coreService.getRegion();
        const formatter = this.formatter.percent(dataForNumber, locale);
        const text = formatter(value);
        return text;
    }

    public formatCurrency(value: any, currencyCode?: string): any {
        currencyCode = currencyCode ||  'USD';
        value = this.validateNumber(value, 'formatCurrency');
        const dataForCurrency = this.getPattern(PatternCategories.CURRENCIES)
                                || this.getSourcePattern(PatternCategories.CURRENCIES);
        const locale = this.coreService.getLanguage() + '-' + this.coreService.getRegion();
        const formatter = this.formatter.currencies(dataForCurrency, locale);
        const text = formatter(value, currencyCode);
        return text;
    }

    public getPluralCategoryType(value: number, locale?: string): string | undefined {
        value = this.validateNumber(value, 'Plural in message');
        // try to round number with default number formatting rules
        // if data isn't exist, use origin number.
        // getPattern will fallback to sourceLocale.
        try {
            const dataForNumber = this.getPattern(PatternCategories.NUMBER, locale);
            const formatter = this.formatter.roundNumberForPlural( dataForNumber, locale);
            value = Number(formatter(value));
        } catch (error) { }
        value = Math.abs( value );
        locale = locale ? locale : this.coreService.getLanguage();
        const pluraFunction = this.getPluralFunction(locale);
        const type = pluraFunction ? pluraFunction(value) : undefined;
        return type;
    }

    private getPluralFunction(locale: string) {
        return this.plural.getFunc(locale);
    }

    public getSupportedLanguages(displayLanguage?: string): Promise<Object[] | null> {
        return this.coreService.getSupportedLanguages(displayLanguage);
    }

    public getSupportedRegions(language: string): Promise<Object | null> {
        return this.coreService.getSupportedRegions(language);
    }
}
