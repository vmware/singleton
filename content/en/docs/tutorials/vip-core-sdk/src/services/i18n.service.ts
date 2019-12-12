/**
 * @copyright Copyright VMware, Inc. All rights reserved. VMware Confidential.
 * @license For licensing, see LICENSE.md.
 */

import { DateFormatter } from '../formatters/date.formatter';
import { NumberFormatter } from '../formatters/number.formatter';
import { CoreService } from './core.service';
import { Store } from '../cache';
import { Constants } from '../constants';
import { Logger, basedLogger} from '../logger';


export class I18nService {
    private sourceI18nData: any;
    private scope: any;
    private pluralFunction: any;
    private pluralFunctionForSourceLang: any;
    private logger: Logger;

    constructor(
        private coreService: CoreService,
        private dateFormatter: DateFormatter,
        private cacheManager: Store,
    ) {
        this.sourceI18nData = this.coreService.getSourcePattern();
        this.scope = this.coreService.getI18nScope();
        this.pluralFunctionForSourceLang = this.sourceI18nData.plurals.pluralFn;
        this.logger = basedLogger.create('I18nService');
    }

    private getPatternDataFromCache(): any {
        return this.cacheManager.lookforPattern(this.coreService.getLanguage(),
            this.coreService.getRegion());
    }

    private validateNumber(value: number | string): number {
        let number;
        if (typeof value === 'string' && !isNaN(+value - parseFloat(value))) {
            number = +value;
        } else if (typeof value !== 'number') {
            throw new Error(`'${value}' is not a number`);
        } else {
            number = value;
        }
        return number;
    }

    public formatDate(value: any, pattern: string = 'mediumDate', timezone?: string): any {
        const date = this.dateFormatter.getStandardTime(value);
        const type = Object.prototype.toString.call(date);
        if ((type !== '[object Date]') || !isFinite(date.getTime())) {
            throw new Error(`InvalidArgument: '${date}' for 'formatDate'`);
        }
        const dataForDate = this.validateFormatTypeInScope('dates');
        return this.dateFormatter.getformattedString(date, pattern, dataForDate, '-', timezone);
    }

    public formatNumber(value: any, language?: string): string {
        let dataForNumber = this.validateFormatTypeInScope('numbers');
        if ( language && language === Constants.SOURCE_LANGUAGE ) {
            dataForNumber = this.sourceI18nData.numbers;
        }
        value = this.validateNumber(value);
        return NumberFormatter(value, dataForNumber, 'decimal');
    }

    public formatPercent(value: any): string {
        const dataForNumber = this.validateFormatTypeInScope('numbers');
        value = this.validateNumber(value);
        return NumberFormatter(value, dataForNumber, 'percent');
    }

    public formatCurrency(value: any, currencyCode: string = 'USD'): any {
        try {
            this.validateCurrencyCode(currencyCode);
        } catch (e) {
            this.logger.error(`currency code ${currencyCode} not found`);
        }

        const dataForCurrency = this.getFormatsDataInCacheForCurrencies(currencyCode);
        value = this.validateNumber(value);
        let text = NumberFormatter(value, dataForCurrency, 'currency');
        text = text.replace(/\u00A4/g, dataForCurrency.currencySymbol);
        return text;
    }
    // Verify that the type exists in the initial i18nScope configuration.
    private validateFormatTypeInScope(type: string): Object {
        if (Constants.SOURCE_REGION !== this.coreService.getRegion() && this.scope.indexOf(type) === -1) {
            throw new Error(`You should add "${type}" to 'i18nScope' in initialize configuration`);
        }
        // patternData fallback to sourceI18nData only valid for number and date formatting.
        const patternData = this.getPatternDataFromCache();
        if (type === 'plurals' && patternData && patternData[type].pluralFn) {
            this.pluralFunction = patternData[type].pluralFn;
        }
        const data = patternData && patternData[type]
            ? patternData[type] : this.sourceI18nData[type];
        return data;
    }

    private getFormatsDataInCacheForCurrencies(currencyCode: string) {
        // currency format relies on currency and numberformat data
        this.validateFormatTypeInScope('currencies');
        const patternDataFromCache = this.getPatternDataFromCache();

        let formatsData;
        const patternData = patternDataFromCache,
            sourceI18nData = this.sourceI18nData;
        const patternDataForCurrentRegion = patternData && patternData.numbers && patternData.currencies;
        // If numbers or currency data not exist, fallback to sourceI18nData
        formatsData = patternDataForCurrentRegion ? patternData : sourceI18nData;

        const dataForNumber = formatsData.numbers;
        const supplementalCurrencies = this.sourceI18nData.supplemental.currencies;
        let symbol = currencyCode;
        let fractions = 2;

        if (formatsData.currencies[currencyCode]) {
            symbol = formatsData.currencies[currencyCode].symbol;
            fractions = supplementalCurrencies.fractions[currencyCode] || null;
        }

        dataForNumber.currencySymbol = symbol;
        dataForNumber.fractions = fractions;
        return dataForNumber;
    }

    private validateCurrencyCode(code: string) {
        const reg = /^[A-Z]{3}$/;
        const is3capital = reg.test(code);
        const codeList = this.sourceI18nData.currencies;
        if (!codeList[code] || !is3capital) {
            throw Error('Unsupported currency code');
        }
    }

    public getPluralCategoryType(value: number, language?: string): string | undefined {
        this.validateFormatTypeInScope('plurals');
        value = this.validateNumber(value);
        value = Math.abs(value);

        language = language ? language : this.coreService.getLanguage();
        if (this.coreService.isSourceLanguage(language)) {
            return this.pluralFunctionForSourceLang(value);
        }

        const type = this.pluralFunction ? this.pluralFunction(value) : undefined;
        return type;
    }

    public getSupportedLanguages(displayLanguage?: string): Promise<Object[] | null> {
        return this.coreService.getSupportedLanguages(displayLanguage);
    }

    public getSupportedRegions(language: string): Promise<Object | null> {
        return this.coreService.getSupportedRegions(language);
    }
}
