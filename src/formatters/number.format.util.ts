/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
export enum CurrenciesDataType {
    DIGIST = '_digits',
    ROUNDING = '_rounding'
}
export enum RoundingMode {
    ROUND_UP,
    ROUND_DOWN,
    ROUND_CEIL,
    ROUND_FLOOR,
    ROUND_HALF_UP,
    ROUND_HALF_DOWN,
    ROUND_HALF_EVEN,
    ROUND_HALF_CEIL,
    ROUND_HALF_FLOOR,
    EUCLID
}

export enum NumberFormatTypes {
    DECIMAL = 'decimal',
    PERCENT = 'percent',
    CURRENCIES = 'currencies',
    PLURAL = 'plural'
}

export interface Symbols {
    decimal: string;
    exponential: string;
    group: string;
    infinity: string;
    list: string;
    minusSign: string;
    nan: string;
    perMille: string;
    percentSign: string;
    plusSign: string;
    superscriptingExponent: string;
    timeSeparator: string;
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
