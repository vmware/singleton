/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Logger, basedLogger } from './logger';

interface ResType {
    [key: string]: any;
}

export abstract class ResponseParser {
    abstract validateResponse( res: ResType ): Object|null;
    abstract getPatterns( res: ResType ): Object|null;
    abstract getTranslations( res: ResType ): Object|null;
    abstract getSupportedRegions?( res: ResType ): Object|null;
    abstract getSupportedLanguages?( res: ResType ): {}[]|null;
}


 class VIPResponseParser implements ResponseParser {
    private logger: Logger;
    constructor() {
        this.logger = basedLogger.create('VIPResponseParser');
    }
    validateResponse( res: ResType ): any {
        if ( !res || !res.response ) {
            return null;
        }
        const response = res.response;
        // response code is bussiness code from VIP backend.
        if ( response.code !== 200 ) {
            this.logger.error( response.message );
        }
        return res.data;
    }
    getPatterns(res: ResType): Object | null {
        const data = this.validateResponse(res);
        const pattern = data && data.pattern && data.pattern.categories
            ? data.pattern.categories : null;
        return pattern;
    }
    getTranslations(res: ResType): Object | null {
        const data = this.validateResponse(res);
        const translations = data && data.messages ? data.messages :
            data && data.components ? this.getMessages(data.components) : null; 
        return translations;
    }
    getMessages(components: ResType) {
        var result = {};
        components.forEach((component: ResType) => {
            result = { ...result, ...component.messages };
        })
        return result;
    }
    getSupportedLanguages( res: ResType ): {}[]|null {
        const data = this.validateResponse( res );
        const languages = data && data.languages ? data.languages : null;
        return languages;
    }
    getSupportedRegions( res: ResType ): Object|null {
        const data = this.validateResponse( res );
        const regions = data &&  data[0] && data[0].territories ? data[0].territories : null;
        return regions;
    }
}

export const defaultResponseParser = new VIPResponseParser();
