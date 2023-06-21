/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Decimal } from 'decimal.js-light';
import { NumberFormatTypes, DataForNumber, DataForCurrency, RoundingMode, CurrenciesDataType } from './number.format.util';
import { isDefined } from '../utils';
const DECIMAL_SEP = '.';
const ZERO_CHAR = '0';
const GROUP_SEP = ',';
const DIGIT_CHAR = '#';
const PATTERN_SEP = ';';

interface ParsedNumber {
    // an array of digits containing leading zeros as necessary
    digits: number[];
    // the number of the digits in `digits` that are to the left of the decimal point
    integerLen: number;
}

class Formatter {
    decimal(data: DataForNumber): Function {
        const decimalFormats = data.numberFormats.decimalFormats,
            symbol = data.numberSymbols;
        let formatsInfo = this.parseFormats(decimalFormats);
        return (value: number, min?: number, max?: number) => {
            if (isDefined(max) || isDefined(min)) {
                formatsInfo = this.resetFormats(formatsInfo, min, max, RoundingMode.ROUND_HALF_EVEN);
            }
            return this.resetString(false, formatsInfo, symbol, value, max, min);
        };
    }
    currencies(data: DataForCurrency): Function {
        const currencyFormats = data.currencyFormats,
            symbol = data.numberSymbols;
        return (value: number, currencyCode: string, min?: number, max?: number) => {
            let formatsInfo = this.parseFormats(currencyFormats);
            formatsInfo = this.resetCurrencyFormatsInfo(formatsInfo, data, currencyCode, min, max);
            const res = this.resetString(true, formatsInfo, symbol, value, max, min);
            const currencySymbol = data.currencySymbols[currencyCode] && data.currencySymbols[currencyCode].symbol
                ? data.currencySymbols[currencyCode].symbol
                : currencyCode;
            return res.replace(/\u00A4/g, currencySymbol);
        };
    }
    percent(data: DataForNumber): Function {
        const percentFormats = data.numberFormats.percentFormats,
            symbol = data.numberSymbols;
        let formatsInfo = this.parseFormats(percentFormats);
        return (value: number, min?: number, max?: number) => {
            if (isDefined(max) || isDefined(min)) {
                formatsInfo = this.resetFormats(formatsInfo, min, max, RoundingMode.ROUND_HALF_EVEN);
            }
            value = +this.resetPercentNumber(value);
            return this.resetString(false, formatsInfo, symbol, value, max, min);
        };
    }
    plural(data: DataForNumber): Function {
        const decimalFormats = data.numberFormats.decimalFormats;
        let formatsInfo = this.parseFormats(decimalFormats);
        return (value: number, min?: number, max?: number) => {
            if (isDefined(max) || isDefined(min)) {
                formatsInfo = this.resetFormats(formatsInfo, min, max, RoundingMode.ROUND_HALF_EVEN);
            }
            return this.roundingNumber(value, formatsInfo.minFrac, formatsInfo.maxFrac, formatsInfo.round);
        };
    }
    resetFormats(formats: any, minFracDigit: number, maxFracDigit: number, round: RoundingMode) {
        let minFraction = formats.minFrac;
        let maxFraction = formats.maxFrac;
        if (isDefined(minFracDigit)) {
            minFraction = Math.ceil(minFracDigit) ? minFracDigit : minFraction;
        }
        if (isDefined(maxFracDigit)) {
            maxFraction = Math.ceil(maxFracDigit) ? maxFracDigit : minFraction;
        }
        if (isDefined(maxFracDigit) && minFraction > maxFraction) {
            maxFraction = minFraction;
        }
        formats.minFrac = minFraction;
        formats.maxFrac = maxFraction;
        if (isDefined(round)) {
            formats.round = round;
        }
        return formats;
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
        const integer = positiveParts[0],
            fraction = positiveParts[1] || '';

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
            const trunkLen = positive.length - patternInfo.posPre.length - patternInfo.posSuf.length,
                pos = negative.indexOf(DIGIT_CHAR);

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

    resetCurrencyFormatsInfo(formatsInfo: any, data: DataForCurrency, currencyCode: string, min?: number, max?: number) {
        if (!data.fractions[currencyCode]) {
            return formatsInfo;
        }
        const minFrac = data.fractions[currencyCode][CurrenciesDataType.DIGIST] || formatsInfo.minFrac;
        formatsInfo.minFrac = isDefined(min) ? min : minFrac;
        formatsInfo.maxFrac = isDefined(max) ? max : formatsInfo.minFrac;

        const rounding = data.fractions[currencyCode][CurrenciesDataType.ROUNDING];
        formatsInfo.round = !rounding || rounding === '0' ? formatsInfo.round : rounding;
        return formatsInfo;
    }

    resetPercentNumber(num: number) {
        return new Decimal(num).times(100).valueOf();
    }

    resetString(isCurrency: boolean, formatsInfo: any, symbol: any, value: any, minFracDigit?: number, maxFracDigit?: number): string {
        let formattedText: string;
        let minFraction = formatsInfo.minFrac;
        let maxFraction = formatsInfo.maxFrac;
        if (minFracDigit != null) {
            minFraction = Math.ceil(minFracDigit) ? minFracDigit : minFraction;
        }
        if (maxFracDigit != null) {
            maxFraction = Math.ceil(maxFracDigit) ? maxFracDigit : minFraction;
        } else if (maxFracDigit != null && minFraction > maxFraction) {
            maxFraction = minFraction;
        }

        let numberStr = this.roundingNumber(Math.abs(value), minFraction, maxFraction, formatsInfo.round);
        if (!isCurrency) {
            numberStr = String(+numberStr);
        }
        const parsedNumber = this.parseNumber(numberStr);

        let digits = parsedNumber.digits;
        const integerLen = parsedNumber.integerLen;
        let decimals = [];

        // extract decimals digits
        if (integerLen > 0) {
            decimals = digits.splice(integerLen, digits.length);
        } else {
            decimals = digits;
            digits = [0];
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
            formatter = this.formatter.currencies(data);
            this.mapping.get(locale).set(NumberFormatTypes.CURRENCIES, formatter);
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
            formatter = this.formatter.decimal(data);
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
