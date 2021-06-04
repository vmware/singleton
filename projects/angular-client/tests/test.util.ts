/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { I18nLoader } from '../index';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

export const baseConfig = {
    productID : 'vipngxsample',
    component: 'default',
    version: '1.0.0',
    host: 'test',
    isPseudo: false,
    sourceBundles: [{
        'demo.string.two': '{0, plural,one {VIP Angular client has a user.} other {VIP Angular client has # users.}}',
        'application.title': 'Welcome to VIP Angular sample application!',
        'demo.string.one': 'VIP Angular client supports both {0} and {1}',
        'test.no.translation': 'source string',
        'test.with.param': '{0} source string',
    }]
};

@Injectable()
export class TestLoader implements I18nLoader {
    constructor() { }
    getLocaleData(): Observable<any> {
        return of(null);
    }
    getSupportedLanguages(): Observable<Object> {
        return of([]);
    }
    getSupportedRegions(): Observable<Object> {
        return of([]);
    }
    getLocalizedCities(): Observable<Object> {
        return of([]);
    }
}
