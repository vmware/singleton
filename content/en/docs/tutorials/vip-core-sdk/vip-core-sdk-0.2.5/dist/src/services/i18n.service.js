"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const number_formatter_1 = require("../formatters/number.formatter");
const plural_formatter_1 = require("../formatters/plural/plural.formatter");
const configuration_1 = require("../configuration");
const locale_en_1 = require("../data/locale_en");
const utils_1 = require("../utils");
const exceptions_1 = require("../exceptions");
class I18nService {
    constructor(coreService, dateFormatter, cacheManager) {
        this.coreService = coreService;
        this.dateFormatter = dateFormatter;
        this.cacheManager = cacheManager;
        this.formatter = new number_formatter_1.FormatterFactory();
        this.plural = new plural_formatter_1.Plural();
        // init source pattern data.
        this.sourceI18nData = locale_en_1.default;
        this.sourceI18nData.categories[configuration_1.PatternCategories.CURRENCIES] = this.getCurrencyData(this.sourceI18nData.categories);
    }
    validateScope(type) {
        if (this.coreService.getI18nScope().indexOf(type) === -1) {
            throw new Error(`You should add '${type}' to 'i18nScope' in initialize configuration`);
        }
        return true;
    }
    getPattern(type, locale) {
        const language = this.coreService.getLanguage(), region = this.coreService.getRegion();
        const isSourceLocale = locale && this.coreService.isSourceLocale(locale);
        if (isSourceLocale || this.coreService.isSourceLocale(language, region)) {
            return this.getSourcePattern(type);
        }
        this.validateScope(type);
        const localeData = this.cacheManager.lookforPattern(this.coreService.getLanguage(), this.coreService.getRegion());
        if (localeData && type === configuration_1.PatternCategories.CURRENCIES) {
            // number formatting is part of currency format.
            localeData[configuration_1.PatternCategories.CURRENCIES] = this.getCurrencyData(localeData);
        }
        let data;
        if (localeData && localeData[type]) {
            data = this.isEmptyObject(localeData[type]) ? undefined : localeData[type];
        }
        return data;
    }
    isEmptyObject(obj) {
        if (!utils_1.isDefined(obj)) {
            return true;
        }
        const keys = Object.keys(obj);
        return keys.length ? false : true;
    }
    getSourcePattern(type) {
        return this.sourceI18nData.categories[type];
    }
    getCurrencyData(data) {
        if (this.isEmptyObject(data) || this.isEmptyObject(data[configuration_1.PatternCategories.CURRENCIES])) {
            return undefined;
        }
        // TODO: data sharing.  Avoid duplicate processing.
        if (data[configuration_1.PatternCategories.CURRENCIES].currencyFormats) {
            return data[configuration_1.PatternCategories.CURRENCIES];
        }
        const isNumberPatternExist = data[configuration_1.PatternCategories.NUMBER] && !this.isEmptyObject(data[configuration_1.PatternCategories.NUMBER]);
        return isNumberPatternExist ? {
            currencyFormats: data[configuration_1.PatternCategories.NUMBER].numberFormats.currencyFormats,
            numberSymbols: data[configuration_1.PatternCategories.NUMBER].numberSymbols,
            currencySymbols: data[configuration_1.PatternCategories.CURRENCIES],
            fractions: data['supplemental'].currencies.fractions
        } : null;
    }
    validateNumber(value, type) {
        if (!this.validateInput(value)) {
            throw exceptions_1.invalidParamater(`Invalid number '${value}' for '${type}'`);
        }
        let number;
        if (typeof value === 'string' && !isNaN(+value - parseFloat(value))) {
            number = +value;
        }
        else if (typeof value !== 'number') {
            throw exceptions_1.invalidParamater(`Invalid number '${value}' for '${type}'`);
        }
        else {
            number = value;
        }
        return number;
    }
    validateInput(value) {
        return utils_1.isDefined(value) && value === value;
    }
    formatDate(value, pattern = 'mediumDate', timezone) {
        if (!this.validateInput(value)) {
            throw exceptions_1.invalidParamater(`Invalid date '${value}' for 'formatDate'`);
        }
        const date = this.dateFormatter.getStandardTime(value);
        const type = Object.prototype.toString.call(date);
        if ((type !== '[object Date]') || !isFinite(date.getTime())) {
            throw exceptions_1.invalidParamater(`Invalid date '${value}' for 'formatDate'`);
        }
        const dataForDate = this.getPattern(configuration_1.PatternCategories.DATE) || this.getSourcePattern(configuration_1.PatternCategories.DATE);
        return this.dateFormatter.getformattedString(date, pattern, dataForDate, '-', timezone);
    }
    formatNumber(value, locale) {
        const dataForNumber = this.getPattern(configuration_1.PatternCategories.NUMBER, locale) || this.getSourcePattern(configuration_1.PatternCategories.NUMBER);
        value = this.validateNumber(value, 'formatNumber');
        const formatter = this.formatter.decimal(dataForNumber, locale);
        const text = formatter(value);
        return text;
    }
    formatPercent(value) {
        const dataForNumber = this.getPattern(configuration_1.PatternCategories.NUMBER) || this.getSourcePattern(configuration_1.PatternCategories.NUMBER);
        value = this.validateNumber(value, 'formatPercent');
        const locale = this.coreService.getLanguage() + '-' + this.coreService.getRegion();
        const formatter = this.formatter.percent(dataForNumber, locale);
        const text = formatter(value);
        return text;
    }
    formatCurrency(value, currencyCode) {
        currencyCode = currencyCode || 'USD';
        value = this.validateNumber(value, 'formatCurrency');
        const dataForCurrency = this.getPattern(configuration_1.PatternCategories.CURRENCIES)
            || this.getSourcePattern(configuration_1.PatternCategories.CURRENCIES);
        const locale = this.coreService.getLanguage() + '-' + this.coreService.getRegion();
        const formatter = this.formatter.currencies(dataForCurrency, locale);
        const text = formatter(value, currencyCode);
        return text;
    }
    getPluralCategoryType(value, locale) {
        value = this.validateNumber(value, 'Plural in message');
        // try to round number with default number formatting rules
        // if data isn't exist, use origin number.
        // getPattern will fallback to sourceLocale.
        try {
            const dataForNumber = this.getPattern(configuration_1.PatternCategories.NUMBER, locale);
            const formatter = this.formatter.roundNumberForPlural(dataForNumber, locale);
            value = Number(formatter(value));
        }
        catch (error) { }
        value = Math.abs(value);
        locale = locale ? locale : this.coreService.getLanguage();
        const pluraFunction = this.getPluralFunction(locale);
        const type = pluraFunction ? pluraFunction(value) : undefined;
        return type;
    }
    getPluralFunction(locale) {
        return this.plural.getFunc(locale);
    }
    getSupportedLanguages(displayLanguage) {
        return this.coreService.getSupportedLanguages(displayLanguage);
    }
    getSupportedRegions(language) {
        return this.coreService.getSupportedRegions(language);
    }
}
exports.I18nService = I18nService;
