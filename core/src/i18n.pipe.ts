/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Pipe, PipeTransform, OnDestroy } from '@angular/core';
import { I18nService } from './services/i18n.service';
import { Subscription } from 'rxjs';
import { isDefined, equals } from './util';
import { invalidPipeArgumentError } from './exceptions';
import { NumberFormatOptions } from './formatters/number.format.model';

@Pipe({ name: 'I18nPipe' })
export class I18nPipe implements OnDestroy {
    constructor() { }
    protected value: string;
    protected lastInput: any;
    protected onPatternChange: Subscription;
    protected _dispose(): void {
        if (typeof this.onPatternChange !== 'undefined') {
            this.onPatternChange.unsubscribe();
            this.onPatternChange = undefined;
        }
    }
    ngOnDestroy() {
        this._dispose();
    }
}

@Pipe({ name: 'numberFormat', pure: false })
export class NumberFormatPipe extends I18nPipe implements PipeTransform {
    constructor(private i18nService: I18nService) {
        super();
    }

    updateValue(value: any, locale?: string, formatOptions?: NumberFormatOptions) {
        const onChange = (currentLocale: string) => {
            try {
                this.value = this.i18nService.formatNumber(value, currentLocale, formatOptions);
            } catch (error) {
                throw invalidPipeArgumentError(NumberFormatPipe, error.message);
            }
        };

        if (locale) {
            onChange(locale);
        } else {
            this.i18nService.current.subscribe(onChange);
        }
    }

    transform(value: any, formatOptions?: NumberFormatOptions): string | null {

        if (!isDefined(value)) {
            return null;
        }

        if (equals(this.lastInput, arguments)) {
            return this.value;
        }

        this.lastInput = arguments;
        // locale and formatOptions are optional parameters,
        // the locale will be current locale if the parameter is passed as 'undefined'.
        this.updateValue(value, undefined, formatOptions);
        this._dispose();

        if (!this.onPatternChange) {
            this.onPatternChange = this.i18nService.onLocaleChange.subscribe(
                (locale: string) => {
                    this.updateValue(value, locale, formatOptions);
                }
            );
        }

        return this.value;
    }
}

@Pipe({ name: 'currencyFormat', pure: false })
export class CurrencyFormatPipe extends I18nPipe implements PipeTransform {
    constructor(private i18nService: I18nService) {
        super();
    }

    updateValue(amount: any, currencyCode: string = 'USD', locale?: string, formatOptions?: NumberFormatOptions) {
        const onChange = (currentLocale: string) => {
            try {
                this.value = this.i18nService.formatCurrency(amount, currencyCode, currentLocale, formatOptions);
            } catch (error) {
                throw invalidPipeArgumentError(CurrencyFormatPipe, error.message);
            }
        };

        if (locale) {
            onChange(locale);
        } else {
            this.i18nService.current.subscribe(onChange);
        }
    }

    transform(amount: any, currencyCode: string, formatOptions?: NumberFormatOptions): string | null {
        if (!isDefined(amount)) {
            return null;
        }

        if (equals(this.lastInput, arguments)) {
            return this.value;
        }

        this.lastInput = arguments;
        // locale and formatOptions are optional parameters,
        // the locale will be current locale if the parameter is passed as 'undefined'.
        this.updateValue(amount, currencyCode, undefined, formatOptions);
        this._dispose();

        if (!this.onPatternChange) {
            this.onPatternChange = this.i18nService.onLocaleChange.subscribe(
                (locale: string) => {
                    this.updateValue(amount, currencyCode, locale, formatOptions);
                }
            );
        }
        return this.value;
    }
}


@Pipe({ name: 'percentFormat', pure: false })
export class PercentFormatPipe extends I18nPipe implements PipeTransform {

    constructor(private i18nService: I18nService) {
        super();
    }

    updateValue(value: any, locale?: string, formatOptions?: NumberFormatOptions) {
        const onChange = (currentLocale: string) => {
            try {
                this.value = this.i18nService.formatPercent(value, currentLocale, formatOptions);
            } catch (error) {
                throw invalidPipeArgumentError(PercentFormatPipe, error.message);
            }
        };

        if (locale) {
            onChange(locale);
        } else {
            this.i18nService.current.subscribe(onChange);
        }
    }

    transform(value: any, formatOptions?: NumberFormatOptions): string | null {
        if (!isDefined(value)) {
            return null;
        }
        if (equals(this.lastInput, arguments)) {
            return this.value;
        }

        this.lastInput = arguments;
        // locale and formatOptions are optional parameters,
        // the locale will be current locale if the parameter is passed as 'undefined'.
        this.updateValue(value, undefined, formatOptions);
        this._dispose();

        if (!this.onPatternChange) {
            this.onPatternChange = this.i18nService.onLocaleChange.subscribe(
                (locale: string) => {
                    this.updateValue(value, locale, formatOptions);
                }
            );
        }

        return this.value;
    }
}


@Pipe({ name: 'dateFormat', pure: false })
export class DateFormatPipe extends I18nPipe implements PipeTransform {
    constructor(private i18nService: I18nService) {
        super();
    }

    updateValue(value: any, pattern: string, timezone?: string, locale?: string) {
        const onChange = (currentLocale: string) => {
            try {
                timezone = timezone ? timezone : null;
                const formattedDate = this.i18nService.formatDate(value, pattern, currentLocale, timezone);
                this.value = formattedDate;
            } catch (error) {
                throw invalidPipeArgumentError(DateFormatPipe, error.message);
            }
        };

        if (locale) {
            onChange(locale);
        } else {
            this.i18nService.current.subscribe(onChange);
        }
    }

    transform(value: any, pattern: string = 'mediumDate', timezone?: string): string | null {
        if (!isDefined(value)) {
            return null;
        }

        if (equals(this.lastInput, arguments)) {
            return this.value;
        }

        this.lastInput = arguments;
        this.updateValue(value, pattern, timezone);
        this._dispose();

        if (!this.onPatternChange) {
            this.onPatternChange = this.i18nService.onLocaleChange.subscribe(
                (locale: string) => {
                    this.updateValue(value, pattern, timezone, locale);
                }
            );
        }
        if (typeof value === 'string') {
            value = value.trim();
        }
        return this.value;
    }
}
