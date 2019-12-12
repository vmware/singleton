export declare class DateFormatter {
    private i18nData;
    constructor();
    /**
     * Convert number or string to standard time
     * @param date
     * @return instance of Date or origin string
    */
    getStandardTime(date: any): any;
    /**
     * Get locale date time from standard date string
     * @return Formatted locale date time string
     */
    getformattedString(date: Date, pattern: string, i18nData: any, minusSign?: string, timezone?: string): string;
    /**
     * Get rules from pattern by type
     * @return The function to get locale string
     */
    private getRules;
    private getRulesInPattern;
    /**
     * The date-time pattern shows how to combine separate patterns for date (represented by {1})
     * and time (represented by {0}) into a single pattern.
     */
    private formatDateTimeRules;
    /**
     * Get locale date string from pattern
     */
    private dateStrGetter;
    private getLocaleString;
    /**
     * Get periods of the day
     */
    private getDaysPeriods;
    /**
     * Get eras
     */
    private getEras;
    /**
     * Convert pattern string to pattern array
     */
    private patternFilter;
    private concat;
    /**
     * Corresponding function of the rule
     */
    private getFormatFunctionByRule;
}
export declare const defaultDateFormatter: DateFormatter;
