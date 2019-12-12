/**
 * verify value if undefined or null
 */

export function isDefined(value: any) {
    return value !== undefined && value !== null && value !== '';
}

export function resolveLanguageTag(languageTag: string) {
    if (!isDefined(languageTag)) {
        return languageTag;
    }
    return languageTag.split('_').join('-').toLocaleLowerCase();
}

declare interface Window {
    navigator: any;
}
declare const window: Window;
/**
 * Returns the culture language code name from the browser, e.g. "de-DE"
 * @returns string
 */
export function getBrowserCultureLang(): string {
    if (window === undefined || window.navigator === undefined) {
        return undefined;
    }

    let browserCultureLang: any = window.navigator.languages ? window.navigator.languages[0] : null;
    browserCultureLang = browserCultureLang ||
        window.navigator.language ||
        window.navigator.browserLanguage ||
        window.navigator.userLanguage;
    return browserCultureLang;
}
