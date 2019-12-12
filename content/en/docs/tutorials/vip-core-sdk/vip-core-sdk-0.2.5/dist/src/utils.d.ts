/**
 * verify value if undefined or null
 */
export declare function isDefined(value: any): boolean;
export declare function resolveLanguageTag(languageTag: string): string;
/**
 * Returns the culture language code name from the browser, e.g. "de-DE"
 * @returns string
 */
export declare function getBrowserCultureLang(): string;
/**
 * Merge the key-value pair which defined in each object and
 * check whether the key is globally unique
 * TODO: Support object directly
 * @param target Target object contains all strings
 * @param source A set of source objects
 */
export declare function assign(target: {
    [x: string]: any;
}, source: {
    [x: string]: any;
}[]): {
    [x: string]: any;
};
