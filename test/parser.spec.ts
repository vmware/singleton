/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import {defaultResponseParser} from '../src/parser';

const rsp = {
    response: { code: 200 },
    data: { 
        key: 'value',
        categories: 'patterns',
        messages: 'success',
        languages: ['en-US'],
    },
};
const rsp2 = {
    response: { code: 200 },
    data: [{territories:'US'}]
};
const rsp3 = {
    response: { code: 200 },
    data: { 
        pattern: {categories: 'patterns'},
    },
};

describe('Parser Module', () => {
    it("validateResponse",() => {
      expect(defaultResponseParser.validateResponse({data:{}})).toEqual(null);
      expect(defaultResponseParser.validateResponse(rsp)).toEqual(rsp.data);
    });
    it("getPatterns",() => {
        expect(defaultResponseParser.getPatterns({data:{}})).toEqual(null);
        expect(defaultResponseParser.getPatterns(rsp3)).toEqual(rsp.data.categories);
    });
    it("getTranslations",() => {
        expect(defaultResponseParser.getTranslations({data:{}})).toEqual(null);
        expect(defaultResponseParser.getTranslations(rsp)).toEqual(rsp.data.messages);
    });
    it("getSupportedLanguages",() => {
        expect(defaultResponseParser.getSupportedLanguages({data:{}})).toEqual(null);
        expect(defaultResponseParser.getSupportedLanguages(rsp)).toEqual(rsp.data.languages);
    });
    it("getSupportedRegions",() => {
        expect(defaultResponseParser.getSupportedRegions({data:{}})).toEqual(null);
        expect(defaultResponseParser.getSupportedRegions(rsp2)).toEqual(rsp2.data[0].territories);
    });
});