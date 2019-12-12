import { Decimal } from 'decimal.js-light';

const enum CurrenciesDataType {
    DIGIST = '_digits',
    ROUNDING = '_rounding'
}
const enum RoundingMode {
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

const DECIMAL_SEP = '.';
const ZERO_CHAR = '0';
const GROUP_SEP = ',';
const DIGIT_CHAR = '#';
const PATTERN_SEP = ';';
let roundingMode: RoundingMode = RoundingMode.ROUND_HALF_EVEN;


export function NumberFormatter(value: number,
                                localeData: any,
                                type: string = 'decimal',
                                minFracDigit?: number,
                                maxFracDigit?: number): string {
    const numberSymbols = localeData.numberSymbols,
        format = localeData.numberFormats[ type + 'Formats'],
        pattern = parseNumberFormat(format, numberSymbols.minusSign),
        groupSep = numberSymbols.group,
        decimalSep = numberSymbols.decimal;
    let number: any = value;
    const isInfinity = !isFinite(number);
    let isZero: boolean;
    let formattedText = '',
        parsedNumber;

    if ( isNaN(value) ) {
        return null;
     }
    if (isInfinity) {
        formattedText = '\u221e';
    } else {
        if ( type === 'currency' && localeData.fractions) {
            pattern.minFrac = localeData.fractions[CurrenciesDataType.DIGIST] || pattern.minFrac;
            pattern.maxFrac = pattern.minFrac;

            const rounding = localeData.fractions[CurrenciesDataType.ROUNDING];
            roundingMode =  rounding === '0' || !rounding ? roundingMode : rounding;
        }

        let minFraction = pattern.minFrac;
        let maxFraction = pattern.maxFrac;
        if ( minFracDigit != null ) {
            minFraction = Math.ceil(minFracDigit) ? minFracDigit : minFraction;
        }
        if ( maxFracDigit != null ) {
            maxFraction = Math.ceil(maxFracDigit) ? maxFracDigit : minFraction;
        } else if ( maxFracDigit != null &&  minFraction > maxFraction) {
            maxFraction = minFraction;
        }
        if ( type === 'percent') {
            number = new Decimal(number).times(100).valueOf();
        }

        number = roundNumber( +number, minFraction, maxFraction, roundingMode);
        const numStr = +number < 0 ? number.substr(1) : number;
        parsedNumber = parse(numStr);

        let digits = parsedNumber.digits;
        const integerLen = parsedNumber.integerLen;
        let decimals = [];
        isZero = digits.every(d => !d);

        // extract decimals digits
        if (integerLen > 0) {
            decimals = digits.splice(integerLen, digits.length);
        } else {
            decimals = digits;
            digits = [0];
        }

        // format the integer digits with grouping separators
        const groups = [];
        if (digits.length >= pattern.lgSize) {
            groups.unshift(digits.splice(-pattern.lgSize, digits.length).join(''));
        }
        while (digits.length > pattern.gSize) {
            groups.unshift(digits.splice(-pattern.gSize, digits.length).join(''));
        }
        if (digits.length) {
            groups.unshift(digits.join(''));
        }
        formattedText = groups.join(groupSep);

        // append the decimal digits
        if (decimals.length) {
            formattedText += decimalSep + decimals.join('');
        }
    }
    if (number < 0 && !isZero) {
        return pattern.negPre + formattedText + pattern.negSuf;
    } else {
        return pattern.posPre + formattedText + pattern.posSuf;
    }
}
/**
 * Round the parsed number to the specified number of decimal places
 * This function changed the parsedNumber in-place
 */
interface ParsedNumber {
    // an array of digits containing leading zeros as necessary
    digits: number[];
    // the number of the digits in `digits` that are to the left of the decimal point
    integerLen: number;
}

/**
 *
 * (Significant bits of this parse algorithm came from https://github.com/MikeMcl/big.js/)
 *
 * @param The number to parse
 */
function parse(numStr: string): ParsedNumber {
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
 * Get format info from the pattern
 */
function parseNumberFormat(format: string, minusSign = '-') {
    const patternInfo = {
        'minInt': 1,
        'minFrac': 0,
        'maxFrac': 0,
        'posPre': '',
        'posSuf': '',
        'negPre': '',
        'negSuf': '',
        'gSize': 0,
        'lgSize': 0
    };
    const patternParts = format.split(PATTERN_SEP);
    const positive = patternParts[0];
    const negative = patternParts[1];

    const positiveParts = positive.indexOf(DECIMAL_SEP) !== -1 ?
    positive.split(DECIMAL_SEP) :
    [
    positive.substring(0, positive.lastIndexOf(ZERO_CHAR) + 1),
    positive.substring(positive.lastIndexOf(ZERO_CHAR) + 1)
    ],
    integer = positiveParts[0], fraction = positiveParts[1] || '';

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

function roundNumber(number: number, minFrac: number, maxFrac: number, mode: RoundingMode) {
    const digists = number.toString().replace(DECIMAL_SEP, '').length;
    const decimalIndex = number.toString().indexOf(DECIMAL_SEP);
    const numberOfIntegerDigits = decimalIndex > -1 ? decimalIndex : digists;
    const fractionLen = digists - numberOfIntegerDigits;
    if (minFrac > maxFrac) {
        throw new Error(
            `The minimum number of digits after fraction (${minFrac}) is higher than the maximum (${maxFrac}).`);
    }
    const newDecimal: Decimal = new Decimal(number);
    const fractionSize =  Math.min(Math.max(minFrac, fractionLen), maxFrac);
    const roundedNum = newDecimal.toFixed(fractionSize, mode);
    return roundedNum;
}
