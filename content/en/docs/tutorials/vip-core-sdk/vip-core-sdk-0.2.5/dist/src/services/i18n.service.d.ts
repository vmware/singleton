import { DateFormatter } from '../formatters/date.formatter';
import { CoreService } from './core.service';
import { Store } from '../cache';
export declare class I18nService {
    private coreService;
    private dateFormatter;
    private cacheManager;
    private sourceI18nData;
    private formatter;
    private plural;
    constructor(coreService: CoreService, dateFormatter: DateFormatter, cacheManager: Store);
    private validateScope;
    private getPattern;
    private isEmptyObject;
    private getSourcePattern;
    private getCurrencyData;
    private validateNumber;
    private validateInput;
    formatDate(value: any, pattern?: string, timezone?: string): any;
    formatNumber(value: any, locale?: string): string;
    formatPercent(value: any): string;
    formatCurrency(value: any, currencyCode?: string): any;
    getPluralCategoryType(value: number, locale?: string): string | undefined;
    private getPluralFunction;
    getSupportedLanguages(displayLanguage?: string): Promise<Object[] | null>;
    getSupportedRegions(language: string): Promise<Object | null>;
}
