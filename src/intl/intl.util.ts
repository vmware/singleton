/*
* Copyright 2020 VMware, Inc.
* SPDX-License-Identifier: EPL-2.0
*/

import { invalidParamater } from '../exceptions';
import { isDefined } from '../util';

// Interface for PatterData
export interface IPatternData {
    dates: any;
    numbers: any;
    plurals: any;
    currencies: any;
}

// Enum for CurrenciesDataType
export enum CurrenciesDataType {
    DIGIST = '_digits',
    ROUNDING = '_rounding'
}

// Enum for RoundingMode
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

// Interface for Symbols
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

// Enum for NumberFormatTypes
export enum NumberFormatTypes {
    DECIMAL = 'decimal',
    PERCENT = 'percent',
    CURRENCIES = 'currencies',
    PLURAL = 'plural'
}

export function validateNumber(value: number | string, type: string): number {
    if (!validateInput(value)) {
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

function validateInput( value: any ) {
    return isDefined( value ) && value === value;
}


