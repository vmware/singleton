export const config = {
    productID: 'CoreSDKClient',
    version: '1.0.0',
    component: 'ui',
    // The host of the Singleton service
    host: 'http://localhost:8091',
    // The language cookie name 
    langCookieName: 'vcs_locale',
    // The attribute in the DOM elements that holds the key for the translation.
    localizeAttribute: 'l10n',
    // The pseudo key for the localstorage 
    localStoragePseudoKey: 'enable_localization_pseudo'
};