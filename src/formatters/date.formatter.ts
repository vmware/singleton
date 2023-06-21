/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
export class DateFormatter {
    private i18nData: any;
    constructor() { }
    /**
     * Convert number or string to standard time
     * @param date
     * @return instance of Date or origin string
    */
    public getStandardTime(date: any): any {
        const NUMBER_STRING = /^-?\d+$/;
        if (typeof date === 'string') {
            date = NUMBER_STRING.test(date) ? toInt(date) : getDateByString(date);
        }
        if (typeof date === 'number') {
            date = new Date(date);
        }
        return date;
    }

    /**
     * Get locale date time from standard date string
     * @return Formatted locale date time string
     */
    public getformattedString(date: Date, pattern: string, i18nData: any, minusSign: string = '-', timezone?: string): string {
        this.i18nData = i18nData;
        const rules = this.getRules(pattern) ? this.getRules(pattern) : pattern;
        const parts = this.patternFilter(rules);
        let dateTimezoneOffset = date.getTimezoneOffset();
        if (timezone) {
            dateTimezoneOffset = timezoneToOffset(timezone, dateTimezoneOffset);
            date = convertTimezoneToLocal(date, timezone);
        }
        let text = '';
        parts.forEach(value => {
            const fn = this.getFormatFunctionByRule(value, minusSign);
            text += fn ? fn(date, dateTimezoneOffset)
                : value === '\'\'' ? '\'' : value.replace(/(^'|'$)/g, '').replace(/''/g, '\'');
        });
        return text;
    }

    /**
     * Get rules from pattern by type
     * @return The function to get locale string
     */
    private getRules(pattern: string): string | undefined {
        let rules;
        switch (pattern) {
            case 'shortTime': rules = this.getRulesInPattern('time', 'short');
                break;
            case 'mediumTime': rules = this.getRulesInPattern('time', 'medium');
                break;
            case 'longTime': rules = this.getRulesInPattern('time', 'long');
                break;
            case 'fullTime': rules = this.getRulesInPattern('time', 'full');
                break;
            case 'shortDate': rules = this.getRulesInPattern('date', 'short');
                break;
            case 'mediumDate': rules = this.getRulesInPattern('date', 'medium');
                break;
            case 'longDate': rules = this.getRulesInPattern('date', 'long');
                break;
            case 'fullDate': rules = this.getRulesInPattern('date', 'full');
                break;
            case 'short':
                const shortDate = this.getRulesInPattern('date', 'short');
                const shortTime = this.getRulesInPattern('time', 'short');
                rules = this.formatDateTimeRules('short', [shortTime, shortDate]);
                break;
            case 'medium':
                const mediumDate = this.getRulesInPattern('date', 'medium');
                const mediumTime = this.getRulesInPattern('time', 'medium');
                rules = this.formatDateTimeRules('medium', [mediumTime, mediumDate]);
                break;
            case 'full':
                const fullDate = this.getRulesInPattern('date', 'full');
                const fullTime = this.getRulesInPattern('time', 'full');
                rules = this.formatDateTimeRules('full', [fullTime, fullDate]);
                break;
            case 'long':
                const longDate = this.getRulesInPattern('date', 'long');
                const longTime = this.getRulesInPattern('time', 'long');
                rules = this.formatDateTimeRules('long', [longTime, longDate]);
                break;
            default:
                rules = this.getRulesInPattern('dateTime', pattern);
        }
        return rules;
    }

    private getRulesInPattern(name: string, type: string) {
        const rules = name !== 'dateTime' ? this.i18nData[name + 'Formats'][type]
            : this.i18nData[name + 'Formats']['availableFormats'][type];
        return rules;
    }
    /**
     * The date-time pattern shows how to combine separate patterns for date (represented by {1})
     * and time (represented by {0}) into a single pattern.
     */
    private formatDateTimeRules(type: string, rulesArray: string[]) {
        const format = this.i18nData.dateTimeFormats[type];
        const rules = format.trim().replace(/\{(\d)\}/g, (match: string, key: string) => {
            return (rulesArray != null && rulesArray[+key]) ? rulesArray[+key] : match;
        });
        return rules;
    }

    /**
     * Get locale date string from pattern
     */
    private dateStrGetter(name: string, width: string, type: string = 'format') {
        return (date: Date) => {
            let text = '';
            switch (name) {
                case 'Day': text = this.getLocaleString(date, name, width, type);
                    break;
                case 'Month': text = this.getLocaleString(date, name, width, type);
                    break;
                case 'dayPeriods': text = this.getDaysPeriods(date, name, width);
                    break;
                case 'eras': text = this.getEras(date, name, width);
                    break;
            }
            return text;
        };
    }
    private getLocaleString(date: Date, name: string, width: string, type: any = 'format') {
        const firstLetter: string = type.slice(0, 1);
        type = type.replace(firstLetter, firstLetter.toUpperCase());
        const namestr = name + 's';
        const patternName = namestr.toLowerCase() + type;
        const value = getPartOfDate(date, name);
        const pattern = this.i18nData[patternName][width];
        return pattern[value];
    }
    /**
     * Get periods of the day
     */
    private getDaysPeriods(date: Date, name: string, width: string) {
        const pattern = this.i18nData.dayPeriodsFormat[width];
        return date.getHours() < 12 ? pattern[0] : pattern[1];
    }
    /**
     * Get eras
     */
    private getEras(date: Date, name: string, width: string) {
        const pattern = this.i18nData.eras[width];
        return date.getFullYear() < 0 ? pattern[0] : pattern[1];
    }
    /**
     * Convert pattern string to pattern array
     */
    private patternFilter(pattern: string): any[] {
        let parts = [],
            match;
        const mainWord = 'GyMLwWdEahHKmsSzZO',
            wordRange =
            'G{1,5}|y{1,4}|M{1,5}|L{1,5}|w{1,2}|W{1}|d{1,2}|E{1,6}|a{1,5}|h{1,2}|H{1,2}|K{1,2}|m{1,2}|s{1,2}|S{1,3}|x|z{1,4}|Z{1,5}|O{1,4}';
        const reg = `((?:[^` + mainWord + `']+)|(?:'(?:[^']|'')*')|(?:` + wordRange + '))([\\s\\S]*)';
        const DATE_FORMATS_SPLIT = new RegExp(reg);
        while (pattern) {
            match = DATE_FORMATS_SPLIT.exec(pattern);
            if (match) {
                parts = this.concat(parts, match, 1);
                pattern = parts.pop();
            } else {
                parts.push(pattern);
                pattern = null;
            }
        }
        return parts;
    }
    private concat(array1: any[], array2: any[], index: number): any[] {
        return array1.concat([].slice.call(array2, index));
    }
    /**
     * Corresponding function of the rule
     */
    private getFormatFunctionByRule(rule: string, minusSign: string) {
        let fn = null;
        switch (rule) {
            // Era string for the current date
            case 'G':
            case 'GG':
            case 'GGG':
                fn = this.dateStrGetter('eras', 'abbreviated');
                break;
            case 'GGGG':
                fn = this.dateStrGetter('eras', 'wide');
                break;
            case 'GGGGG':
                fn = this.dateStrGetter('eras', 'narrow');
                break;
            // Calendar year (numeric).
            case 'y':
                fn = dateGetter('FullYear', 1, 0, false, true);
                break;
            case 'yy':
                fn = dateGetter('FullYear', 2, 0, true, true);
                break;
            case 'yyy':
                fn = dateGetter('FullYear', 3, 0, false, true);
                break;
            case 'yyyy':
                fn = dateGetter('FullYear', 4, 0, false, true);
                break;
            // Month number/name, format style (intended to be used in conjunction with ‘d’ for day number).
            case 'M':
            case 'L':
                fn = dateGetter('Month', 1, 1);
                break;
            case 'MM':
            case 'LL':
                fn = dateGetter('Month', 2, 1);
                break;
            case 'MMM':
                fn = this.dateStrGetter('Month', 'abbreviated');
                break;
            case 'MMMM':
                fn = this.dateStrGetter('Month', 'wide');
                break;
            case 'MMMMM':
                fn = this.dateStrGetter('Month', 'narrow');
                break;
            // Stand-Alone Month number/name (intended to be used without ‘d’ for day number).
            case 'LLL':
                fn = this.dateStrGetter('Month', 'abbreviated', 'standalone');
                break;
            case 'LLLL':
                fn = this.dateStrGetter('Month', 'wide', 'standalone');
                break;
            case 'LLLLL':
                fn = this.dateStrGetter('Month', 'narrow', 'standalone');
                break;
            // Week of Year (numeric).
            case 'w':
                fn = weekGetter(1);
                break;
            case 'ww':
                fn = weekGetter(2);
                break;
            // Week of Month (numeric)
            case 'W':
                fn = weekGetter(1, true);
                break;
            // Day of month (numeric).
            case 'd':
                fn = dateGetter('Date', 1);
                break;
            case 'dd':
                fn = dateGetter('Date', 2);
                break;
            // Day of week name, format style.
            case 'E':
            case 'EE':
            case 'EEE':
                fn = this.dateStrGetter('Day', 'abbreviated');
                break;
            case 'EEEE':
                fn = this.dateStrGetter('Day', 'wide');
                break;
            case 'EEEEE':
                fn = this.dateStrGetter('Day', 'narrow');
                break;
            case 'EEEEEE':
                fn = this.dateStrGetter('Day', 'short');
                break;
            // AM, PM
            case 'a':
            case 'aa':
            case 'aaa':
                fn = this.dateStrGetter('dayPeriods', 'abbreviated');
                break;
            case 'aaaa':
                fn = this.dateStrGetter('dayPeriods', 'wide');
                break;
            case 'aaaaa':
                fn = this.dateStrGetter('dayPeriods', 'narrow');
                break;
            // Hour [1-12].
            case 'h':
                fn = dateGetter('Hours', 1, -12);
                break;
            case 'hh':
                fn = dateGetter('Hours', 2, -12);
                break;
            // Hour [0-23].
            case 'H':
                fn = dateGetter('Hours', 1);
                break;
            case 'HH':
                fn = dateGetter('Hours', 2);
                break;
            // Hour K [0-11]
            case 'K':
                fn = dateGetter('Hours', 1, -13);
                break;
            case 'KK':
                fn = dateGetter('Hours', 2, -13);
                break;
            // Minute (numeric).
            case 'm':
                fn = dateGetter('Minutes', 1);
                break;
            case 'mm':
                fn = dateGetter('Minutes', 2);
                break;
            // Second (numeric).
            case 's':
                fn = dateGetter('Seconds', 1);
                break;
            case 'ss':
                fn = dateGetter('Seconds', 2);
                break;
            // Fractional second padded
            case 'S':
                fn = dateGetter('Milliseconds', 1);
                break;
            case 'SS':
                fn = dateGetter('Milliseconds', 2);
                break;
            case 'SSS':
                fn = dateGetter('Milliseconds', 3);
                break;
            case 'x':
                fn = dateGetter('UnixTimeStamp');
                break;
            // Timezone ISO8601 short format (-0430)
            case 'Z':
            case 'ZZ':
            case 'ZZZ':
                fn = timeZoneGetter('short', minusSign);
                break;
            case 'ZZZZZ':
                fn = timeZoneGetter('extended', minusSign);
                break;
            // Timezone GMT short format (GMT+4)
            case 'O':
            case 'OO':
            case 'OOO':
            case 'z':
            case 'zz':
            case 'zzz':
                fn = timeZoneGetter('shortGMT', minusSign);
                break;
            case 'OOOO':
            case 'ZZZZ':
            case 'zzzz':
                fn = timeZoneGetter('long', minusSign);
                break;
            default:
                return null;
        }
        return fn;
    }
}

/**
 * Format date from iso8601 time string
 */
function IsoToDate(match: any[]) {
    const date = new Date(0),
        dateSetter = match[8] ? date.setUTCFullYear : date.setFullYear,
        timeSetter = match[8] ? date.setUTCHours : date.setHours;
    let tzHour = 0,
        tzMin = 0;
    if (match[9]) {
        tzHour = toInt(match[9] + match[10]);
        tzMin = toInt(match[9] + match[11]);
    }
    dateSetter.call(date, toInt(match[1]), toInt(match[2]) - 1, toInt(match[3]));
    const h = toInt(match[4] || 0) - tzHour;
    const m = toInt(match[5] || 0) - tzMin;
    const s = toInt(match[6] || 0);
    const ms = Math.round(parseFloat('0.' + (match[7] || 0)) * 1000);
    timeSetter.call(date, h, m, s, ms);
    return date;
}
function padNumber(num: number | string, digits: number, trim?: boolean, negWrap?: boolean) {
    if (!digits) {
        return num;
    }
    const ZERO_CHAR = '0';
    let neg = '';
    if (num < 0 || (negWrap && num <= 0)) {
        if (negWrap) {
            num = -num + 1;
        } else {
            num = -num;
            neg = '-';
        }
    }
    num = '' + num;
    while (num.length < digits) {
        num = ZERO_CHAR + num;
    }
    if (trim) {
        num = num.substr(num.length - digits);
    }
    return neg + num;
}
/**
 * Convert string to standard date string
 * @param dateString  eg '2017,12,06'  R_ISO8601_STR-> '2015/06/15T09:03:01+0900'
 * @return Date / string
 */
function getDateByString(dateString: string): any {
    let string: string;
    const isBrowserSide = typeof window !== 'undefined' && this === window;
    const isIE = isBrowserSide && navigator.userAgent.indexOf('Trident');
    const isSafari = isBrowserSide && navigator.userAgent.indexOf('Safari') > -1 && navigator.userAgent.indexOf('Chrome') < 0;

    string = dateString.indexOf('/') ? dateString.replace(/\//g, '-') : dateString;

    if ((isSafari || isIE) && string.indexOf('T') < 0) {
        string = string.replace(/-/g, '/');
    }

    let match: any;
    const R_ISO8601_STR = /^(\d{4})-?(\d\d)-?(\d\d)(?:T(\d\d)(?::?(\d\d)(?::?(\d\d)(?:\.(\d+))?)?)?(Z|([+-])(\d\d):?(\d\d))?)?$/;
    if ((match = string.match(R_ISO8601_STR))) {
        return IsoToDate(match);
    } else {
        const date = new Date(string);
        if (date.toString() === 'Invalid Date') {
            return dateString;
        }
        return date;
    }
}
/**
 *  Get date string from date
 * @param name
 * @param size
 * @param offset
 * @param trim
 * @param negWrap
 */
function dateGetter(name: string, size?: number, offset?: number, trim?: boolean, negWrap?: boolean) {
    offset = offset || 0;
    return function (date: Date) {
        let value = getPartOfDate(date, name);
        if (offset > 0 || value > -offset) {
            value += offset;
        }
        if (value === 0 && offset === -12) {
            value = 12;
        }
        return padNumber(value, size, trim, negWrap);
    };
}

function getPartOfDate(date: Date, name: string): number {
    switch (name) {
        case 'FullYear':
            return date.getFullYear();
        case 'Month':
            return date.getMonth();
        case 'Date':
            return date.getDate();
        case 'Hours':
            return date.getHours();
        case 'Minutes':
            return date.getMinutes();
        case 'Seconds':
            return date.getSeconds();
        case 'Milliseconds':
            return date.getMilliseconds();
        case 'UnixTimeStamp':
            return date.valueOf();
        case 'Day':
            return date.getDay();
        default:
            throw new Error(`Unknown DateType value "${name}".`);
    }
}

/**
 * Get time difference from timezone
*/
function timezoneToOffset(timezone: string, fallback: number): number {
    const ALL_COLONS = /:/g;
    // Support: IE 9-11 only, Edge 13-15+
    // IE/Edge do not "understand" colon (`:`) in timezone
    timezone = timezone.replace(ALL_COLONS, '');
    const requestedTimezoneOffset = Date.parse('Jan 01, 1970 00:00:00 ' + timezone) / 60000;
    const isNaN = Number.isNaN || function isNumberNaN(num) {
        return num !== num;
    };
    return isNaN(requestedTimezoneOffset) ? fallback : requestedTimezoneOffset;
}

function convertTimezoneToLocal(date: Date, timezone: string): Date {
    // get Time difference between local time and GMT
    const dateTimezoneOffset = date.getTimezoneOffset();
    // get Time difference between timezone time and GMT
    const timezoneOffset = timezoneToOffset(timezone, dateTimezoneOffset);
    return addDateMinutes(date, -1 * (timezoneOffset - dateTimezoneOffset));
}

/**
 *Returns a date formatter that transforms a date and an offset into a timezone with ISO8601 or
 * GMT format depending on the width (eg: short = +0430, short:GMT = GMT+4, long = GMT+04:30)
 */
function timeZoneGetter(type: string, minusSign: string) {
    return (date: Date, offset: number) => {
        const zone = -1 * offset;
        const hours = zone > 0 ? Math.floor(zone / 60) : Math.ceil(zone / 60);
        switch (type) {
            case 'short':
                return ((zone >= 0) ? '+' : '') + padNumber(hours, 2) +
                    padNumber(Math.abs(zone % 60), 2);
            case 'shortGMT':
                return 'GMT' + ((zone >= 0) ? '+' : '') + padNumber(hours, 1);
            case 'long':
                return 'GMT' + ((zone >= 0) ? '+' : '') + padNumber(hours, 2) + ':' +
                    padNumber(Math.abs(zone % 60), 2);
            case 'extended':
                if (offset === 0) {
                    return 'Z';
                } else {
                    return ((zone >= 0) ? '+' : '') + padNumber(hours, 2) + ':' +
                        padNumber(Math.abs(zone % 60), 2);
                }
        }
    };
}
function addDateMinutes(date: Date, minutes: number): Date {
    date = new Date(date.getTime());
    date.setMinutes(date.getMinutes() + minutes);
    return date;
}
function getFirstThursdayOfYear(year: number): Date {
    // 0 = index of January
    const dayOfWeekOnFirst = (new Date(year, 0, 1)).getDay();
    // 4 = index of Thursday (+1 to account for 1st = 5)
    // 11 = index of *next* Thursday (+1 account for 1st = 12)
    return new Date(year, 0, ((dayOfWeekOnFirst <= 4) ? 5 : 12) - dayOfWeekOnFirst);
}

function getThursdayThisWeek(datetime: Date) {
    return new Date(datetime.getFullYear(), datetime.getMonth(),
        // 4 = index of Thursday
        datetime.getDate() + (4 - datetime.getDay()));
}

function weekGetter(size: number, isWeek: boolean = false) {
    return function (date: Date) {
        let result;
        if (isWeek) {
            const nbDaysBefore1stDayOfMonth =
                new Date(date.getFullYear(), date.getMonth(), 1).getDay() - 1;
            const today = date.getDate();
            result = 1 + Math.floor((today + nbDaysBefore1stDayOfMonth) / 7);
        } else {
            const firstThurs = getFirstThursdayOfYear(date.getFullYear()),
                thisThurs = getThursdayThisWeek(date);
            const diff = +thisThurs - +firstThurs;
            result = 1 + Math.round(diff / 6.048e8); // 6.048e8 ms per week
        }
        return padNumber(result, size);
    };
}

function toInt(str: string): number {
    return parseInt(str, 10);
}

export const defaultDateFormatter = new DateFormatter();
