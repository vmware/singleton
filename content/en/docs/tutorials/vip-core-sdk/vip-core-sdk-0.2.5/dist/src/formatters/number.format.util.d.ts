export declare enum CurrenciesDataType {
    DIGIST = "_digits",
    ROUNDING = "_rounding"
}
export declare enum RoundingMode {
    ROUND_UP = 0,
    ROUND_DOWN = 1,
    ROUND_CEIL = 2,
    ROUND_FLOOR = 3,
    ROUND_HALF_UP = 4,
    ROUND_HALF_DOWN = 5,
    ROUND_HALF_EVEN = 6,
    ROUND_HALF_CEIL = 7,
    ROUND_HALF_FLOOR = 8,
    EUCLID = 9
}
export declare enum NumberFormatTypes {
    DECIMAL = "decimal",
    PERCENT = "percent",
    CURRENCIES = "currencies",
    PLURAL = "plural"
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
    currencySymbols: {
        [key: string]: any;
    };
    fractions: {
        [currencyCode: string]: {
            _digits: string;
            _rounding: string;
        };
    };
    currencyFormats: string;
    numberSymbols: Symbols;
}
export interface DataForNumber {
    numberFormats: {
        percentFormats: string;
        decimalFormats: string;
    };
    numberSymbols: Symbols;
}
