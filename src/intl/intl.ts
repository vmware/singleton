/*
* Copyright 2020 VMware, Inc.
* SPDX-License-Identifier: EPL-2.0
* I18n namespace includes DateTimeFormat, NumberFormat, Plural objects.
*/

import { DateFormatter } from '../formatters/date.formatter';
import { FormatterFactory } from '../formatters/number.formatter';
import { PatternCategories } from '../configuration';
import { PLURALFUNCS } from '../formatters/plural/plurals.func';
import { IPatternData, Symbols, NumberFormatTypes, validateNumber} from './intl.util';
import { isDefined, isEmptyObject } from '../util';

export namespace I18n {
    /*
    User needs to register locale data by calling the registerLocaleData function
    before calling other oprations like DateTimeFormat.format() and NumberFormat.format()
    */

    export class PatternData implements IPatternData {
        dates: any;
        numbers: any;
        plurals: any;
        currencies: any;
    }

    function getCurrencyData(data: any): DataForCurrency | null {
        if (isEmptyObject(data) || isEmptyObject(data[PatternCategories.CURRENCIES])) {
            return undefined;
        }
        if (data[PatternCategories.CURRENCIES].currencyFormats) {
            return data[PatternCategories.CURRENCIES];
        }
        const isNumberPatternExist = data[PatternCategories.NUMBER] && !isEmptyObject(data[PatternCategories.NUMBER]);
        return  isNumberPatternExist ? {
            currencyFormats: data[PatternCategories.NUMBER].numberFormats.currencyFormats,
            numberSymbols: data[PatternCategories.NUMBER].numberSymbols,
            currencySymbols: data[PatternCategories.CURRENCIES],
            fractions: data['supplemental'].currencies.fractions
        } : null;
    }

    export let localeData: {[locale: string]: IPatternData} = {};

    export function registerLocaleData(locale: string, pattern: IPatternData) {

        if (!isDefined(locale)) {
            throw new Error(
                `RegisterLocaleData failed, locale is not provided when calling registerLocaleData function.`);
        }
        if (isEmptyObject(pattern)) {
            throw new Error(
                `RegisterLocaleData failed, pattern is not provided when calling registerLocaleData function.`);
        }
        localeData[locale] = pattern ;
        localeData[locale].currencies = getCurrencyData(pattern);
    }

    // Following codes are for DateTime Object
    export interface DateTimeFormatOptions {
        pattern: string;
        minusSign?: string;
        timezone?: string;
    }


    export class DateTimeFormat {
        private static _instances: {[locale: string]: DateTimeFormat} = {};
        private static _instanceFormatters: {[locale: string]: any} = {};
        private options: DateTimeFormatOptions;
        private locale: string;

        constructor(locale: string, options: DateTimeFormatOptions) {
            this.options = options;
            this.locale = locale;
            if (isEmptyObject(localeData[locale])) {
                throw new Error(`Locale data should be registered before creating DateTimeFormat object.`);
            }
            if (isEmptyObject(localeData[locale].dates)) {
                throw new Error(`Pattern data for Datetime should be provided before creating DatetimeFormat object.`);
            }
            if (!isDefined(options.minusSign)) {
                options.minusSign = '-';
            }
            if (!isDefined(options.pattern)) {
                options.pattern = 'mediumDate';
            }
        }

        public static getInstance(locale:string, options: DateTimeFormatOptions) {
            if (!this._instances.hasOwnProperty(locale)) {
                this._instances[locale] = new DateTimeFormat(locale,options);
                this._instanceFormatters[locale] = new DateFormatter(localeData[locale][PatternCategories.DATE]);

            } else {
                if (!isDefined(options.minusSign)) {
                    options.minusSign = '-';
                }
                if (!isDefined(options.pattern)) {
                    options.pattern = 'mediumDate';
                }
                this._instances[locale].options = options;
            }
            return this._instances[locale];
        }

        public getStandardTime(date: any): any {
            return new DateFormatter(localeData[this.locale][PatternCategories.DATE]).getStandardTime(date);
        }

        public format(date: Date): string {
            return DateTimeFormat._instanceFormatters[this.locale].getformattedString(date, this.options.pattern, 
                this.options.minusSign, this.options.timezone);
        }
    }
    // End for DateTime object

    export interface NumberFormatOptions {
        numberFormatType: string;
        currencyCode?: string;
    }

   export interface DataForCurrency {
        currencySymbols: {[key: string]: any};
        fractions: {
            [currencyCode: string]: {
                _digits: string,
                _rounding: string
            }
        };
        currencyFormats: string;
        numberSymbols: Symbols;
    }

    export interface DataForNumber {
        numberFormats: {
            percentFormats: string,
            decimalFormats: string
        };
        numberSymbols: Symbols;
    }

    export class NumberFormat {
        private static _instances: {[locale: string]: NumberFormat} = {};
        private static _instanceFormatter: any = new FormatterFactory();
        private options: NumberFormatOptions;
        private locale: string;

        constructor (locale: string, options: NumberFormatOptions) {
            this.options = options;
            this.locale = locale;
            if (isEmptyObject(localeData[locale])) {
                throw new Error(`Locale data should be registered before creating NumberFormat object.`);
            }
            if (options.numberFormatType === NumberFormatTypes.DECIMAL || options.numberFormatType === NumberFormatTypes.PERCENT) {
                if (isEmptyObject(localeData[locale].numbers)) {
                    throw new Error(`Pattern data for Number should be provided before creating NumberFormat object.`);
                }
            }
            if (options.numberFormatType === NumberFormatTypes.CURRENCIES) {
                if (isEmptyObject(localeData[locale].currencies) || isEmptyObject(localeData[locale].numbers)) {
                    throw new Error(`Pattern data for Currency and Number should be provided before creating NumberFormat object.`);
                }
                if (!isDefined(options.currencyCode)) {
                    options.currencyCode = 'USD';
                }
            } 
        }

        public static getInstance(locale:string, options: NumberFormatOptions) {
            if (!isDefined(options.currencyCode)) {
                options.currencyCode = 'USD';
            }
            if (!this._instances.hasOwnProperty(locale)) {
                this._instances[locale] = new NumberFormat(locale,options);
            } else {
                this._instances[locale].options = options;
            }
            return this._instances[locale];
        }

        public format(value: any) {
            if (!isDefined(value)) {
                value = 0;
            }
            if (this.options.numberFormatType === NumberFormatTypes.DECIMAL) {
                return NumberFormat._instanceFormatter.decimal(localeData[this.locale][PatternCategories.NUMBER], this.locale)(value);
            }
            if (this.options.numberFormatType === NumberFormatTypes.PERCENT) {
                return NumberFormat._instanceFormatter.percent(localeData[this.locale][PatternCategories.NUMBER], this.locale)(value);
            }
            if (this.options.numberFormatType === NumberFormatTypes.PLURAL) {
                return NumberFormat._instanceFormatter.roundNumberForPlural(localeData[this.locale][PatternCategories.NUMBER],
                    this.locale)(value);
            }
            if (this.options.numberFormatType === NumberFormatTypes.CURRENCIES) {
                return NumberFormat._instanceFormatter.currencies(localeData[this.locale][PatternCategories.CURRENCIES],
                    this.locale)(value, this.options.currencyCode);
            }
        }
    }
    // End for NumberFormat

    // Following codes are Plural class
    export class PluralRules {
        private static _instances: {[locale: string]: PluralRules} = {};
        private static _instanceFormatter: any = new FormatterFactory();
        private locale: string;
        
        constructor(locale: string) {
            this.locale = locale;
        }

        public static getInstance(locale:string) {
            if (!this._instances.hasOwnProperty(locale)) {
                this._instances[locale] = new PluralRules(locale);
            } 
            return this._instances[locale];
        }

        private resolveLocale(locale: string): string {
            do {
                if (PLURALFUNCS[locale]) {
                    return locale;
                } else if (PLURALFUNCS[locale.toLocaleLowerCase()]) {
                    return locale.toLocaleLowerCase();
                }
                locale = locale.replace(/(-|)?[^-]*$/, '');
            } while (locale);
            return null;
        }

        private getFunction() {
            return PLURALFUNCS[this.resolveLocale(this.locale)];
        }

        public select(value: number): string | undefined {
            value = validateNumber(value, 'Plural in message');
            // try to round number with default number formatting rules
            // if data isn't exist, use origin number.
            // getPattern will fallback to sourceLocale.
            try {
                const formatter = PluralRules._instanceFormatter.roundNumberForPlural( localeData[this.locale][PatternCategories.NUMBER], 
                    this.locale);
                value = Number(formatter(value));
            } catch (error) { }
            value = Math.abs( value );
            const pluraFunction = this.getFunction();
            const type = pluraFunction ? pluraFunction(value) : undefined;
            return type;
        }    
    }
    // End for Plural
}