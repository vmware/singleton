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
import { DataForCurrency, NumberFormatOptions } from '../formatters/number.format.model';
import { PatternCategories } from '../config';
import { SOURCE_LOCALE_DATA } from '../data/locale_en';
import { BaseService } from './base.service';
import { VIPService, LocaleData } from './vip.service';
import { RelativeTimeFormatter } from '../formatters/relative.time';
import { format, deprecatedWarn } from '../util';
import { VIPServiceConstants } from '../constants';

export interface FormatOption {
    numeric?: string;
    forms?: string;
}

const enum MessagePrefix {
    ALWAYS = 'relativeTime-type-',
    AUTO = 'relative-type-',
    PLURALTYPE = 'relativeTimePattern-count-',
}

@Injectable()
export class I18nService extends BaseService {
    private sourceLocaleData: any;
    private plural: Plural;
    private formatter: FormatterFactory;
    private relativeFormat: RelativeTimeFormatter;
    constructor(
        private dateFormatter: DateFormatter,
        protected localeService: LocaleService,
        protected vipService: VIPService,
        public currentLoader: I18nLoader
    ) {
        super(vipService, localeService);
        this.initSourcePatterns();
        this.formatter = new FormatterFactory();
        this.relativeFormat = new RelativeTimeFormatter();
        this.plural = new Plural();
    }

    public resolveLocaleData(locale: string) {
        let patterns: any;
        let localeData: LocaleData;
        const currentLocale = locale ? locale : this.currentLocale;
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

    public getFormattedDateTime(value: any, pattern: string, timezone?: string): any {
        deprecatedWarn('getFormattedDateTime in I18nService', 'v9', 'formatDate');
        return this.formatDate(value, pattern, undefined, timezone);
    }

    public formatDate(value: any, pattern: string, locale?: string, timezone?: string ): any {
        pattern = isDefined(pattern) ? pattern : 'mediumDate';
        locale = isDefined(locale) ? locale : this.currentLocale;
        const date = this.dateFormatter.getStandardTime(value);
        this.validateDate(date);
        const dataForDate = this.getPattern(PatternCategories.DATE, locale) || this.getSourcePattern(PatternCategories.DATE);
        return this.dateFormatter.getformattedString(date, pattern, dataForDate, '-', timezone);
    }

    /**
     * Get localized pattern by predefined pattern and locale.
     * The default locale is the locale current in use.
     * If no exist date formatting data of the locale, fallback to source locale 'en'.
     * If the pattern is not predefined pattern, return origin pattern directly.
     */
    public getLocalizedPattern( pattern: string, locale?: string ): string {
        locale = isDefined(locale) ? locale : this.currentLocale;
        const dataForDate = this.getPattern(PatternCategories.DATE, locale) || this.getSourcePattern(PatternCategories.DATE);
        const localizedPattern = this.dateFormatter.getRulesByPattern(pattern, dataForDate);
        return localizedPattern;
    }

    /**
     * Get number related formatter from FormatterFactory
     * @param type currencies, currencySymbol, percent or number
     * @param categories PatternCategories
     * @returns formatter
     */
     private getFormatter(type: string, categories: PatternCategories, locale: string) {
        let data = this.getPattern(categories, locale);
        let formatter: any;
        if (data) {
            formatter = this.formatter[type](data, locale);
        } else {
            // fallback to source locale
            data = this.getSourcePattern(categories);
            formatter = this.formatter[type](data, VIPServiceConstants.ENGLISH.languageCode);
        }
        return formatter;
    }

    /**
     * Get the localized currency symbol by currency code and locale,
     * return the currency symbol of the currency code.
     * The default locale is the locale current in use.
     * If the currency code invalid, return the currency code.
     */
    public getLocalizedCurrencySymbol(currencyCode: string, locale?: string) {
        locale = isDefined(locale) ? locale : this.currentLocale;
        const formatter = this.getFormatter('currenySymbol', PatternCategories.CURRENCIES, locale);
        const localizedCurrencySymbol = formatter(currencyCode);
        return localizedCurrencySymbol;
    }

    private validateDate(date: any) {
        const type = Object.prototype.toString.call(date);
        if (!(type === '[object Date]') || !isFinite(date.getTime())) {
            throw new Error(`${date} is not an available date`);
        }
    }

    /**
     * Formats simple relative dates.
     * @param from The starting time of relative time calculation.
     * @param to The ending time of relative time calculation.
     * @param locale The locale to determine which patters need to use.
     * @param options The formatting style.
     */
    public formatRelativeTime( from: Date, to: Date, locale?: string, options?: FormatOption ) {
        locale = isDefined(locale) ? locale : this.currentLocale;
        const timeOffset = this.getRelativeTimeOffset(from, to);
        const res = this.relativeTimeFormat( timeOffset.offset, timeOffset.unit, locale, options);
        return res;
    }

    /**
     * Calculate the most suitable unit and the corresponding offset according to the input time
     * @param from The starting time of relative time calculation.
     * @param to The ending time of relative time calculation.
     */
    private getRelativeTimeOffset(from: Date, to: Date) {
        // Verify the input time
        this.validateDate(from);
        this.validateDate(to);
        const offset = this.relativeFormat.getOffset(from, to);
        return offset;
    }

    /**
     * Get formatted message reaponseable
     * @param offset The offset of time
     * @param unit The unit of offset
     * @param locale The locale to determine which patters need to use.
     * @param options The formatting style.
     */
    private relativeTimeFormat(offset: number, unit: string, locale: string, options: FormatOption ): string {
        const format_numeric = options && options.numeric === 'auto' ? 'auto' : 'always';

        // Get the data of `unit` in the fields node in the date pattern data.
        const dateFields = this.getPattern(PatternCategories.DATEFIELDS, locale) || this.getSourcePattern(PatternCategories.DATEFIELDS);
        const dataOfUnit = dateFields[unit];

        // Get the message corresponding to the offset.
        let messages: string;
        // If `numeric` is automatic, Match the message corresponding to the offset value first.
        if ( format_numeric === 'auto' ) {
            messages =  dataOfUnit [ MessagePrefix.AUTO + offset ];
        }
        // According to the plural type of offset, obtain the message.
        if ( !messages ) {
            const pluralType = this.getPluralCategory(Math.abs(offset), locale);
            const direction = offset > 0 ? 'future' : 'past';
            messages = dataOfUnit[MessagePrefix.ALWAYS + direction][MessagePrefix.PLURALTYPE + pluralType]
                    || dataOfUnit[MessagePrefix.ALWAYS + direction][MessagePrefix.PLURALTYPE + 'other'];
        }
        const res = format(messages, [Math.abs(offset)]);
        return res;
    }

    public getFormattedDecimal(value: any, locale?: string) {
        deprecatedWarn('getFormattedDecimal in I18nService', 'v9', 'formatNumber');
        return this.formatNumber(value, locale);
    }
    public formatNumber(value: any, locale?: string, formatOptions?: NumberFormatOptions): string {
        locale = isDefined(locale) ? locale : this.currentLocale;
        value = this.validateNumber(value);
        const formatter = this.getFormatter('decimal', PatternCategories.NUMBER, locale);
        const text = formatter(value, formatOptions);
        return text;
    }

    public getFormattedPercent(value: any) {
        deprecatedWarn('getFormattedPercent in I18nService', 'v9', 'formatPercent');
        return this.formatPercent(value);
    }
    public formatPercent(value: any, locale?: string, formatOptions?: NumberFormatOptions): string {
        locale = isDefined(locale) ? locale : this.currentLocale;
        value = this.validateNumber(value);
        const formatter = this.getFormatter('percent', PatternCategories.NUMBER, locale);
        const text = formatter(value, formatOptions);
        return text;
    }

    public getFormattedCurrency(value: any, currencyCode: string): any {
        deprecatedWarn('getFormattedCurrency in I18nService', 'v9', 'formatCurrency');
        return this.formatCurrency(value, currencyCode);
    }

    public formatCurrency(value: any, currencyCode?: string, locale?: string, formatOptions?: NumberFormatOptions): any {
        currencyCode = isDefined(currencyCode) ? currencyCode : 'USD';
        locale = isDefined(locale) ? locale : this.currentLocale;
        value = this.validateNumber(value);
        const formatter = this.getFormatter('currencies', PatternCategories.CURRENCIES, locale);
        const text = formatter(value, currencyCode, formatOptions);
        return text;
    }

    /**
     * This APIs will be call by l10n service when render plural message
     * @param value Numerals that need to be dealt with
     * @param locale Locale  from l10n service
     */
    public getPluralCategory(value: number, locale?: string): string | undefined {
        locale = isDefined(locale) ? locale : this.currentLocale;
        value = this.validateNumber(value);
        value = Math.abs(value);
        // try to round number with default number formatting rules
        // if data isn't exist, use origin number.
        // getPattern will use validateScope to validate is correct formatting data exist.
        try {
            const dataForNumber = this.getPattern(PatternCategories.NUMBER, locale);
            const formatter = this.formatter.roundNumberForPlural(dataForNumber, locale);
            value = Number(formatter(value));
        } catch (error) { }
        const pluraFunction = this.getPluralFunction(locale);
        const type = pluraFunction ? pluraFunction(value) : undefined;
        return type;
    }
    public getPluralCategoryType(value: number, locale?: string): string | undefined {
        deprecatedWarn('getPluralCategoryType in I18nService', 'v9', 'getPluralCategory');
        return this.getPluralCategory(value, locale);
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
        const defaultNumberingSystem = data[PatternCategories.NUMBER].defaultNumberingSystem;
        const supplementalData = data.supplemental;
        return data[PatternCategories.NUMBER] ? {
            currencyFormats: data[PatternCategories.NUMBER].numberFormats.currencyFormats,
            numberSymbols: data[PatternCategories.NUMBER].numberSymbols,
            currencySymbols: data[PatternCategories.CURRENCIES],
            fractions: supplementalData.currencies.fractions,
            numberFormats: {
                'decimalFormats-long': data[PatternCategories.NUMBER].numberFormats['decimalFormats-long'],
                'decimalFormats-short': data[PatternCategories.NUMBER].numberFormats['decimalFormats-short']
            },
            defaultNumberingSystem: defaultNumberingSystem,
            numberingSystem: supplementalData.numbers ? supplementalData.numbers.numberingSystems[defaultNumberingSystem] : undefined
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

    async getCities(region: string, language: string) {
        if (!region || !language) {
            return;
        }
        let localizedCities: Object | undefined = await this.currentLoader
            .getLocalizedCities(language, region, this.vipService.mainConfig)
            .toPromise()
            .then(
                (res: any) => {
                    if (isDefined(res) && isDefined(res.cities)) {
                        return res.cities[region];
                    }
                }
            )
            .catch((error: any) => { console.error('Failed to get localized cities from VIP service.', error); });
        return localizedCities;
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
