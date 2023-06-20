/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Decimal } from 'decimal.js-light';
import {
    NumberFormatTypes,
    DataForNumber,
    DataForCurrency,
    RoundingMode,
    CurrenciesDataType,
    NumberFormatOptions
} from './number.format.model';
import { isDefined, isEmptyObject, parseOption } from '../util';
import { Plural } from './plural/plural.formatter';

const DECIMAL_SEP = '.';
const ZERO_CHAR = '0';
const GROUP_SEP = ',';
const DIGIT_CHAR = '#';
const PATTERN_SEP = ';';

const MIN_INTEGER_RANGE = 1;
const MAX_INTEGER_RANGE = 21;
const MIN_FRACTION_RANGE = 0;
const MAX_FRACTION_RANGE = 20;


interface ParsedNumber {
    // an array of digits containing leading zeros as necessary
    digits: number[];
    // the number of the digits in `digits` that are to the left of the decimal point
    integerLen: number;
}

function getCompactData(data: any, display: string) {
    const displayType = display === 'long' ? 'long' : 'short';
    const compactData = data['decimalFormats-' + displayType].decimalFormat;
    let compactMap: any;
    let maxExponent = 0;
    compactMap = Object.keys(compactData).reduce((newItem, compactKey) => {
        const numberExponent = compactKey.split('0').length - 1;
        const pluralForm = compactKey.split('-')[2];
        newItem[numberExponent] = newItem[numberExponent] || {};
        newItem[numberExponent][pluralForm] = compactData[compactKey];
        maxExponent = Math.max(numberExponent, maxExponent);
        return newItem;
    }, {});

    compactMap.maxExponent = maxExponent;
    return compactMap;
}

class Formatter {
    decimal(data: DataForNumber, locale: string) {
        const decimalFormats = data.numberFormats.decimalFormats;
        const symbol = data.numberSymbols;
        const formatsInfo = this.parseFormats(decimalFormats);
        const formatsData = data.numberFormats;
        return (value: number, formatOptions?: NumberFormatOptions) => {
            let currentFormats = formatsInfo;
            let compactData;
            if (!isEmptyObject(formatOptions)) {
                currentFormats = this.resetFormats(formatsInfo, RoundingMode.ROUND_HALF_EVEN, formatOptions);
                if (formatOptions.notation && formatOptions.notation === 'compact') {
                    compactData = getCompactData(formatsData, formatOptions.compactDisplay);
                }
            }
            return this.resetString(currentFormats, symbol, value, compactData, locale);
        };
    }
    currencies(data: DataForCurrency, locale: string) {
        const formatsData = data.numberFormats;
        const currencyFormats = data.currencyFormats;
        const symbol = data.numberSymbols;
        const formatsInfo = this.parseFormats(currencyFormats);
        let nuDigits: string;
        if (data.defaultNumberingSystem !== 'latn') {
            nuDigits = data.numberingSystem._digits || undefined;
        }
        const currencySymbolFormatter = this.currencySymbol(data);
        return (value: number, currencyCode: string, formatOptions?: NumberFormatOptions) => {
            const currentFormats = this.resetCurrencyFormatsInfo(formatsInfo, data, currencyCode, formatOptions);
            let compactData;
            if (!isEmptyObject(formatOptions) && formatOptions.notation === 'compact') {
                compactData = getCompactData(formatsData, formatOptions.compactDisplay);
                compactData.nuDigits = nuDigits;
            }
            const res = this.resetString(currentFormats, symbol, value, compactData, locale);
            const currencySymbol = currencySymbolFormatter(currencyCode);
            return res.replace(/\u00A4/g, currencySymbol);
        };
    }
    currencySymbol(data: DataForCurrency) {
        return (currencyCode: string) => {
            const currencySymbol = data.currencySymbols[currencyCode] && data.currencySymbols[currencyCode].symbol
                ? data.currencySymbols[currencyCode].symbol
                : currencyCode;
            return currencySymbol;
        };
    }
    percent(data: DataForNumber) {
        const percentFormats = data.numberFormats.percentFormats;
        const symbol = data.numberSymbols;
        const formatsInfo = this.parseFormats(percentFormats);
        return (value: number, formatOptions?: NumberFormatOptions) => {
            let currentFormats = formatsInfo;
            if (!isEmptyObject(formatOptions)) {
                currentFormats = this.resetFormats(formatsInfo, RoundingMode.ROUND_HALF_EVEN, formatOptions);
            }
            value = +this.resetPercentNumber(value);
            return this.resetString(currentFormats, symbol, value);
        };
    }
    plural(data: DataForNumber) {
        const decimalFormats = data.numberFormats.decimalFormats;
        let formatsInfo = this.parseFormats(decimalFormats);
        return (value: number, min?: number, max?: number) => {
            if (isDefined(max) || isDefined(min)) {
                formatsInfo = this.resetFormats(formatsInfo, RoundingMode.ROUND_HALF_EVEN);
            }
            return this.roundingNumber(value, formatsInfo.minFrac, formatsInfo.maxFrac, formatsInfo.round);
        };
    }
    resetFormats(formats: any, round: RoundingMode, formatOptions?: NumberFormatOptions) {
        // assign the value type to a new object
        const finalFormats = Object.assign({}, formats);
        // in compact number formats, reset default digits
        if (formatOptions.notation && formatOptions.compactDisplay) {
            finalFormats.maxFrac = 0;
            finalFormats.minFrac = 0;
            finalFormats.minInt = 1;
        }
        if (isDefined(formatOptions.minFractionDigits)) {
            finalFormats.minFrac = parseOption('minFractionDigits', [MIN_FRACTION_RANGE, MAX_FRACTION_RANGE], formatOptions.minFractionDigits);
        }
        if (isDefined(formatOptions.maxFractionDigits)) {
            finalFormats.maxFrac = parseOption('maxFractionDigits', [MIN_FRACTION_RANGE, MAX_FRACTION_RANGE], formatOptions.maxFractionDigits);
        } else if (finalFormats.minFrac !== null && finalFormats.minFrac > finalFormats.maxFrac) {
            // in the currency formatting, if the maximum fraction digit undefined in pattern, set the min as max
            finalFormats.maxFrac = finalFormats.minFrac;
        }
        if (isDefined(formatOptions.minIntegerDigits)) {
            finalFormats.minInt = parseOption('minIntegerDigits', [MIN_INTEGER_RANGE, MAX_INTEGER_RANGE], formatOptions.minIntegerDigits);
        }
        if (isDefined(round)) {
            finalFormats.round = round;
        }
        return finalFormats;
    }
    /**
     * Get info from the formats
     * eg: ¤#,##0.00
     * return: { gSize: 3, lgSize: 3, maxFrac: 2, minFrac: 2, minInt: 1, negPre: "-¤", posPre: "¤" }
     */
    parseFormats(format: string, minusSign = '-') {
        const patternInfo = {
            'minInt': 1,
            'minFrac': 0,
            'maxFrac': 0,
            'posPre': '',
            'posSuf': '',
            'negPre': '',
            'negSuf': '',
            'gSize': 0,
            'lgSize': 0,
            'round': RoundingMode.ROUND_HALF_EVEN
        };
        const patternParts = format.split(PATTERN_SEP);
        const positive = patternParts[0];
        const negative = patternParts[1];

        const positiveParts = positive.indexOf(DECIMAL_SEP) !== -1
            ? positive.split(DECIMAL_SEP)
            : [
                positive.substring(0, positive.lastIndexOf(ZERO_CHAR) + 1),
                positive.substring(positive.lastIndexOf(ZERO_CHAR) + 1)
            ];
        const integer = positiveParts[0];
        const fraction = positiveParts[1] || '';

        patternInfo.posPre = integer.substr(0, integer.indexOf(DIGIT_CHAR));

        for (let i = 0; i < fraction.length; i++) {
            const ch = fraction.charAt(i);
            if (ch === ZERO_CHAR) {
                patternInfo.minFrac = patternInfo.maxFrac = i + 1;
            } else if (ch === DIGIT_CHAR) {
                patternInfo.maxFrac = i + 1;
            } else {
                patternInfo.posSuf += ch;
            }
        }

        const groups = integer.split(GROUP_SEP);
        patternInfo.gSize = groups[1] ? groups[1].length : 0;
        patternInfo.lgSize = (groups[2] || groups[1]) ? (groups[2] || groups[1]).length : 0;

        if (negative) {
            const trunkLen = positive.length - patternInfo.posPre.length - patternInfo.posSuf.length;
            const pos = negative.indexOf(DIGIT_CHAR);

            patternInfo.negPre = negative.substr(0, pos).replace(/'/g, '');
            patternInfo.negSuf = negative.substr(pos + trunkLen).replace(/'/g, '');
        } else {
            patternInfo.negPre = minusSign + patternInfo.posPre;
            patternInfo.negSuf = patternInfo.posSuf;
        }
        return patternInfo;
    }

    parseNumber(numStr: string): ParsedNumber {
        const digits = [];
        let numberOfIntegerDigits;
        let i;

        // Decimal point?
        if ((numberOfIntegerDigits = numStr.indexOf(DECIMAL_SEP)) > -1) {
            numStr = numStr.replace(DECIMAL_SEP, '');
        }

        if (numberOfIntegerDigits < 0) {
            // There was no decimal point or exponent so it is an integer.
            numberOfIntegerDigits = numStr.length;
        }

        for (i = 0; i < numStr.length; i++) {
            digits.push(+numStr.charAt(i));
        }

        return {
            digits: digits,
            integerLen: numberOfIntegerDigits
        };
    }
    /**
     * rounding number
     */
    roundingNumber(number: number, minFrac: number, maxFrac: number, mode: RoundingMode): string {
        // TODO exponent
        const digists = number.toString().replace(DECIMAL_SEP, '').length;
        const decimalIndex = number.toString().indexOf(DECIMAL_SEP);
        const numberOfIntegerDigits = decimalIndex > -1 ? decimalIndex : digists;
        const fractionLen = digists - numberOfIntegerDigits;
        if (minFrac > maxFrac) {
            throw new Error(
                `The minimum number of digits after fraction (${minFrac}) is higher than the maximum (${maxFrac}).`);
        }
        const newDecimal: Decimal = new Decimal(number);
        const fractionSize = Math.min(Math.max(minFrac, fractionLen), maxFrac);
        const roundedNum = newDecimal.toFixed(fractionSize, mode);
        return roundedNum;
    }

    resetCurrencyFormatsInfo(formatsInfo: any, data: DataForCurrency, currencyCode: string, formatOptions?: NumberFormatOptions) {
        if (!data.fractions[currencyCode] && isEmptyObject(formatOptions)) {
            return formatsInfo;
        }
        // assign the value type to a new object
        let finalFormats = Object.assign({}, formatsInfo);
        if (data.fractions[currencyCode]) {
            finalFormats.maxFrac = data.fractions[currencyCode][CurrenciesDataType.DIGIST];
            finalFormats.minFrac = finalFormats.maxFrac;
        }
        const rounding = data.fractions[currencyCode] && data.fractions[currencyCode][CurrenciesDataType.ROUNDING];
        finalFormats.round = !rounding || rounding === '0' ? formatsInfo.round : rounding;
        if (!isEmptyObject(formatOptions)) {
            finalFormats = this.resetFormats(finalFormats, finalFormats.round, formatOptions);
        }
        return finalFormats;
    }

    resetPercentNumber(num: number) {
        return new Decimal(num).times(100).valueOf();
    }

    resetString(formatsInfo: any, symbol: any, value: any, compactData?: any, locale?: string): string {
        let formattedText: string;
        const minFraction = formatsInfo.minFrac;
        const maxFraction = formatsInfo.maxFrac;
        const minInt = formatsInfo.minInt;

        // compact
        let numberExponent;
        let compactPattern;
        if (!isEmptyObject(compactData)) {
            numberExponent = Math.abs(Math.floor(value)).toString().length - 1;
            numberExponent = Math.min(numberExponent, compactData.maxExponent);
            // Use default plural form to perform initial decimal shift
            if (numberExponent >= 3) {
                compactPattern = compactData[numberExponent] && compactData[numberExponent].other;
            }

            // if compactPattern is 0, output the number.
            if (compactPattern === '0') {
                compactPattern = null;
            } else if (compactPattern) {
                const compactDigits = compactPattern.split('0').length - 1;
                const divisor = numberExponent - (compactDigits - 1);
                value = value / Math.pow(10, divisor);
            }
        }

        // number rounding
        let numberStr = this.roundingNumber(Math.abs(value), minFraction, maxFraction, formatsInfo.round);
        // if no min fraction limit, remove meaningless 0
        if (!minFraction) {
            // if the number is >= 1e21
            // use +numberStr will returns a string representing the number in exponential notation.
            numberStr = +numberStr >= 1e21 ? numberStr : String(+numberStr);
        }

        let compactPrefix = '';
        let compactSuffix = '';
        if (compactData && compactPattern) {
            // Get plural form after possible roundings
            const pluralFunc = new Plural().getFunc(locale);
            const pluralForm = pluralFunc && pluralFunc(+value) ? pluralFunc(+value) : 'other';
            compactPattern = compactData[numberExponent][pluralForm] || compactPattern;
            // if the compact pattern contains protected .
            compactPattern = compactPattern.replace('\'.\'', '.');
            const compactProperties = compactPattern.match(/^([^0]*)(0+)([^0]*)$/);
            compactPrefix = compactProperties[1];
            compactSuffix = compactProperties[3];
        }

        // parse the number string
        const parsedNumber = this.parseNumber(numberStr);
        let digits = parsedNumber.digits;
        let integerLen = parsedNumber.integerLen;
        let decimals = [];

        // padding zero for integer if integerLen < minInt
        for (; integerLen < minInt; integerLen++) {
            digits.unshift(0);
        }

        // extract decimals digits
        if (integerLen > 0) {
            decimals = digits.splice(integerLen, digits.length);
        } else {
            decimals = digits;
            digits = [0];
        }

        if (compactData && compactData.nuDigits) {
            digits.forEach((item, idx) => {
                digits[idx] = compactData.nuDigits[+item];
            });
            decimals.forEach((item, idx) => {
                decimals[idx] = compactData.nuDigits[+item];
            });
        }

        // format the integer digits with grouping separators
        const groups = [];
        if (digits.length >= formatsInfo.lgSize) {
            groups.unshift(digits.splice(-formatsInfo.lgSize, digits.length).join(''));
        }
        while (digits.length > formatsInfo.gSize) {
            groups.unshift(digits.splice(-formatsInfo.gSize, digits.length).join(''));
        }
        if (digits.length) {
            groups.unshift(digits.join(''));
        }
        formattedText = groups.join(symbol.group);

        // append the decimal digits
        if (decimals.length) {
            formattedText += symbol.decimal + decimals.join('');
        }

        // append the compact infos
        formattedText = compactPrefix + formattedText + compactSuffix;

        if (value < 0) {
            return formatsInfo.negPre + formattedText + formatsInfo.negSuf;
        } else {
            return formatsInfo.posPre + formattedText + formatsInfo.posSuf;
        }
    }
}


export class FormatterFactory {
    formatter: Formatter;
    private mapping: Map<string, Map<NumberFormatTypes, Function>>
        = new Map<string, Map<NumberFormatTypes, Function>>();
    constructor() {
        this.formatter = new Formatter();
    }
    getFormatter(locale: string, type: NumberFormatTypes): Function {
        if (!this.mapping.get(locale)) {
            this.mapping.set(locale, new Map<NumberFormatTypes, Function>());
        }
        let formatter: Function;
        if (this.mapping.get(locale) && this.mapping.get(locale).get(type)) {
            formatter = this.mapping.get(locale) && this.mapping.get(locale).get(type);
        }
        return formatter;
    }
    currencies(data: DataForCurrency, locale: string) {
        let formatter = this.getFormatter(locale, NumberFormatTypes.CURRENCIES);
        if (!formatter) {
            formatter = this.formatter.currencies(data, locale);
            this.mapping.get(locale).set(NumberFormatTypes.CURRENCIES, formatter);
        }
        return formatter;
    }
    currenySymbol(data: DataForCurrency, locale: string) {
        let formatter = this.getFormatter(locale, NumberFormatTypes.CURRENCYSYMBOL);
        if (!formatter) {
            formatter = this.formatter.currencySymbol(data);
            this.mapping.get(locale).set(NumberFormatTypes.CURRENCYSYMBOL, formatter);
        }
        return formatter;
    }
    percent(data: DataForNumber, locale: string) {
        let formatter = this.getFormatter(locale, NumberFormatTypes.PERCENT);
        if (!formatter) {
            formatter = this.formatter.percent(data);
            this.mapping.get(locale).set(NumberFormatTypes.PERCENT, formatter);
        }
        return formatter;
    }
    decimal(data: DataForNumber, locale: string) {
        let formatter = this.getFormatter(locale, NumberFormatTypes.DECIMAL);
        if (!formatter) {
            formatter = this.formatter.decimal(data, locale);
            this.mapping.get(locale).set(NumberFormatTypes.DECIMAL, formatter);
        }
        return formatter;
    }
    roundNumberForPlural(data: DataForNumber, locale: string) {
        let formatter = this.getFormatter(locale, NumberFormatTypes.PLURAL);
        if (!formatter) {
            formatter = this.formatter.plural(data);
            this.mapping.get(locale).set(NumberFormatTypes.PLURAL, formatter);
        }
        return formatter;
    }
}
