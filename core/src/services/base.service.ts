/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { VIPService } from './vip.service';
import { LocaleService } from './locale.service';
import { EventEmitter } from '@angular/core';
// tslint:disable-next-line: import-blacklist
import { Observable } from 'rxjs';

/**
 * Extract shared methods here for l10n service and i18n service.
 */
export abstract class BaseService {

    constructor(protected vipService: VIPService,
        protected localeService: LocaleService) {
    }

    get stream(): Observable<string | any> {
        return this.vipService.stream;
    }

    get current(): Observable<string | any> {
        return this.vipService.current;
    }

    get currentLocale(): string {
        return this.localeService.getCurrentLocale();
    }

    get onLocaleChange(): EventEmitter<string> {
        return this.vipService.onLocaleChange;
    }
}
