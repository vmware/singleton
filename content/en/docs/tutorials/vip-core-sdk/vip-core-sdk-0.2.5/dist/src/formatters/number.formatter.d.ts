import { NumberFormatTypes, DataForNumber, DataForCurrency, RoundingMode } from './number.format.util';
interface ParsedNumber {
    digits: number[];
    integerLen: number;
}
declare class Formatter {
    decimal(data: DataForNumber): Function;
    currencies(data: DataForCurrency): Function;
    percent(data: DataForNumber): Function;
    plural(data: DataForNumber): Function;
    resetFormats(formats: any, minFracDigit: number, maxFracDigit: number, round: RoundingMode): any;
    /**
     * Get info from the formats
     * eg: ¤#,##0.00
     * return: { gSize: 3, lgSize: 3, maxFrac: 2, minFrac: 2, minInt: 1, negPre: "-¤", posPre: "¤" }
     */
    parseFormats(format: string, minusSign?: string): {
        'minInt': number;
        'minFrac': number;
        'maxFrac': number;
        'posPre': string;
        'posSuf': string;
        'negPre': string;
        'negSuf': string;
        'gSize': number;
        'lgSize': number;
        'round': RoundingMode;
    };
    parseNumber(numStr: string): ParsedNumber;
    /**
     * rounding number
     */
    roundingNumber(number: number, minFrac: number, maxFrac: number, mode: RoundingMode): string;
    resetCurrencyFormatsInfo(formatsInfo: any, data: DataForCurrency, currencyCode: string, min?: number, max?: number): any;
    resetPercentNumber(num: number): string;
    resetString(isCurrency: boolean, formatsInfo: any, symbol: any, value: any, minFracDigit?: number, maxFracDigit?: number): string;
}
export declare class FormatterFactory {
    formatter: Formatter;
    mapping: {
        [key: string]: any;
    };
    constructor();
    getFormatter(locale: string, type: NumberFormatTypes): Function;
    currencies(data: DataForCurrency, locale: string): Function;
    percent(data: DataForNumber, locale: string): Function;
    decimal(data: DataForNumber, locale: string): Function;
    roundNumberForPlural(data: DataForNumber, locale: string): Function;
}
export {};
