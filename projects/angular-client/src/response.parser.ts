/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injectable } from '@angular/core';
import { LocaleData } from '../src/services/vip.service';
import { getNameSpace, VIPConfig } from './config';

interface ResType {
    [key: string]: any;
}

export abstract class ResponseParser {
    abstract validateResponse(res: ResType): Object | undefined;
    abstract ParseLocaleData(res: ResType, config: VIPConfig): Object | undefined;
    abstract ParsePatternsData(res: ResType): Object | undefined;
    abstract ParseTranslationsData(res: ResType): Object | undefined;
    abstract ParseSupportedRegionsData(res: ResType): Object | undefined;
    abstract ParseSupportedLanguagesData(res: ResType): {}[] | undefined;
}


@Injectable()
export class VIPResponseParser implements ResponseParser {
    constructor() { }
    validateResponse(res: ResType): any {
        if ( !res || !res.response ) {
            return null;
        }
        const response = res.response;
        // response code is bussiness code from VIP backend.
        if (response.code && response.code !== 200) {
            throw Error(response.message);
        }
        if ( res.data && res.data.pattern && !res.data.pattern.isExistPattern) {
            res.data.pattern = null;
        }
        return res.data;
    }

    ParseLocaleData(res: ResType, config: VIPConfig): LocaleData | undefined {
        const translations: { [key: string]: any} = {};
        const data = this.validateResponse(res);
        const nameSpace = getNameSpace(config);
        const pattern = this.ParsePatternsData(data);
        translations[nameSpace] = this.ParseTranslationsData(data);
        return {
            categories: pattern,
            messages: translations
        };
    }

    ParsePatternsData(data: ResType): Object | undefined {
        const pattern = data && data.pattern && data.pattern.categories
            ? data.pattern.categories : undefined;
        return pattern;
    }

    ParseTranslationsData(data: ResType): Object | undefined {
        if ( data.messages ) {
            return data.messages;
        }
        const translations = data && data.components && data.components[0].messages
            ? data.components[0].messages : undefined;
        return translations;
    }

    ParseSupportedLanguagesData(res: ResType): {}[] | undefined {
        const data = this.validateResponse(res);
        const languages = data && data.languages ? data.languages : null;
        return languages;
    }
    ParseSupportedRegionsData(res: ResType): Object | undefined {
        const data = this.validateResponse(res);
        const regions = data && data[0] ? data[0] : null;
        return regions;
    }
}
