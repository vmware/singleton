/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { PLURALFUNCS } from '../src/formatters/plural/plurals.func';

describe("PLURALFUNCS",() =>{
    it( 'C[0]', () => {
        expect(PLURALFUNCS['bm'](0,true)).toEqual('other');
        expect(PLURALFUNCS['bm'](0,false)).toEqual('other');
    });
    it( 'C[1]', () => {
        expect(PLURALFUNCS['af'](1,true)).toEqual('other');
        expect(PLURALFUNCS['af'](1,false)).toEqual('one');
        expect(PLURALFUNCS['af'](2,false)).toEqual('other');
    });
    it( 'C[2]', () => {
        expect(PLURALFUNCS['ak'](0,true)).toEqual('other');
        expect(PLURALFUNCS['ak'](0,false)).toEqual('one');
        expect(PLURALFUNCS['ak'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ak'](2,false)).toEqual('other');
    });
    it( 'C[3]', () => {
        expect(PLURALFUNCS['ast'](1.5,true)).toEqual('other');
        expect(PLURALFUNCS['ast'](1.5,false)).toEqual('other');
        expect(PLURALFUNCS['ast'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ast'](2,false)).toEqual('other');
    });
    it( 'am', () => {
        expect(PLURALFUNCS['am'](1,true)).toEqual('other');
        expect(PLURALFUNCS['am'](0,false)).toEqual('one');
        expect(PLURALFUNCS['am'](2,false)).toEqual('other');
    });
    it( 'ar', () => {
        expect(PLURALFUNCS['ar'](1.5,true)).toEqual('other');
        expect(PLURALFUNCS['ar'](0,false)).toEqual('zero');
        expect(PLURALFUNCS['ar'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ar'](2,false)).toEqual('two');
        expect(PLURALFUNCS['ar'](3,false)).toEqual('few');
        expect(PLURALFUNCS['ar'](11,false)).toEqual('many');
        expect(PLURALFUNCS['ar'](100,false)).toEqual('other');
    });
    it( 'ars', () => {
        expect(PLURALFUNCS['ars'](1.5,true)).toEqual('other');
        expect(PLURALFUNCS['ars'](0,false)).toEqual('zero');
        expect(PLURALFUNCS['ars'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ars'](2,false)).toEqual('two');
        expect(PLURALFUNCS['ars'](3,false)).toEqual('few');
        expect(PLURALFUNCS['ars'](11,false)).toEqual('many');
        expect(PLURALFUNCS['ars'](100,false)).toEqual('other');
    });
    it( 'as', () => {
        expect(PLURALFUNCS['as'](9,true)).toEqual('one');
        expect(PLURALFUNCS['as'](3,true)).toEqual('two');
        expect(PLURALFUNCS['as'](4,true)).toEqual('few');
        expect(PLURALFUNCS['as'](6,true)).toEqual('many');
        expect(PLURALFUNCS['as'](11,true)).toEqual('other');
        expect(PLURALFUNCS['as'](0,false)).toEqual('one');
        expect(PLURALFUNCS['as'](2,false)).toEqual('other');
    });
    it( 'az', () => {
        expect(PLURALFUNCS['az'](80,true)).toEqual('one');
        expect(PLURALFUNCS['az'](900,true)).toEqual('few');
        expect(PLURALFUNCS['az'](90,true)).toEqual('many');
        expect(PLURALFUNCS['az'](1000,true)).toEqual('other');
        expect(PLURALFUNCS['az'](1,false)).toEqual('one');
        expect(PLURALFUNCS['az'](2,false)).toEqual('other');
    });
    it( 'be', () => {
        expect(PLURALFUNCS['be'](122,true)).toEqual('few');
        expect(PLURALFUNCS['be'](134,true)).toEqual('other');
        expect(PLURALFUNCS['be'](101,false)).toEqual('one');
        expect(PLURALFUNCS['be'](144,false)).toEqual('few');
        expect(PLURALFUNCS['be'](112,false)).toEqual('many');
        expect(PLURALFUNCS['be'](115,false)).toEqual('many');
        expect(PLURALFUNCS['be'](1.5,false)).toEqual('other');
    });
    it( 'bn', () => {
        expect(PLURALFUNCS['bn'](10,true)).toEqual('one');
        expect(PLURALFUNCS['bn'](3,true)).toEqual('two');
        expect(PLURALFUNCS['bn'](4,true)).toEqual('few');
        expect(PLURALFUNCS['bn'](6,true)).toEqual('many');
        expect(PLURALFUNCS['bn'](11,true)).toEqual('other');
        expect(PLURALFUNCS['bn'](0,false)).toEqual('one');
        expect(PLURALFUNCS['bn'](2,false)).toEqual('other');
    });
    it( 'br', () => {
        expect(PLURALFUNCS['br'](1,true)).toEqual('other');
        expect(PLURALFUNCS['br'](81,false)).toEqual('one');
        expect(PLURALFUNCS['br'](82,false)).toEqual('two');
        expect(PLURALFUNCS['br'](83,false)).toEqual('few');
        expect(PLURALFUNCS['br'](199,false)).toEqual('other');
        expect(PLURALFUNCS['br'](1000000,false)).toEqual('many');
        expect(PLURALFUNCS['br'](1000000.5,false)).toEqual('other');
    });
    it( 'bs', () => {
        expect(PLURALFUNCS['bs'](1,true)).toEqual('other');
        expect(PLURALFUNCS['bs'](21,false)).toEqual('one');
        expect(PLURALFUNCS['bs'](1.01,false)).toEqual('one');
        expect(PLURALFUNCS['bs'](124,false)).toEqual('few');
        expect(PLURALFUNCS['bs'](1.24,false)).toEqual('few');
        expect(PLURALFUNCS['bs'](114,false)).toEqual('other');
    });
    it( 'ca', () => {
        expect(PLURALFUNCS['ca'](3,true)).toEqual('one');
        expect(PLURALFUNCS['ca'](2,true)).toEqual('two');
        expect(PLURALFUNCS['ca'](4,true)).toEqual('few');
        expect(PLURALFUNCS['ca'](5,true)).toEqual('other');
        expect(PLURALFUNCS['ca'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ca'](1.1,false)).toEqual('other');
    });
    it( 'cs', () => {
        expect(PLURALFUNCS['cs'](1,true)).toEqual('other');
        expect(PLURALFUNCS['cs'](1,false)).toEqual('one');
        expect(PLURALFUNCS['cs'](2,false)).toEqual('few');
        expect(PLURALFUNCS['cs'](5.1,false)).toEqual('many');
        expect(PLURALFUNCS['cs'](5.0,false)).toEqual('other');
    });
    it( 'cy', () => {
        expect(PLURALFUNCS['cy'](9,true)).toEqual('zero');
        expect(PLURALFUNCS['cy'](1,true)).toEqual('one');
        expect(PLURALFUNCS['cy'](2,true)).toEqual('two');
        expect(PLURALFUNCS['cy'](4,true)).toEqual('few');
        expect(PLURALFUNCS['cy'](6,true)).toEqual('many');
        expect(PLURALFUNCS['cy'](10,true)).toEqual('other');
        expect(PLURALFUNCS['cy'](0,false)).toEqual('zero');
        expect(PLURALFUNCS['cy'](1,false)).toEqual('one');
        expect(PLURALFUNCS['cy'](2,false)).toEqual('two');
        expect(PLURALFUNCS['cy'](3,false)).toEqual('few');
        expect(PLURALFUNCS['cy'](6,false)).toEqual('many');
        expect(PLURALFUNCS['cy'](7,false)).toEqual('other');
    });
    it( 'da', () => {
        expect(PLURALFUNCS['da'](1,true)).toEqual('other');
        expect(PLURALFUNCS['da'](1.0,false)).toEqual('one');
        expect(PLURALFUNCS['da'](1.1,false)).toEqual('one');
        expect(PLURALFUNCS['da'](2,false)).toEqual('other');
    });
    it( 'dsb', () => {
        expect(PLURALFUNCS['dsb'](1,true)).toEqual('other');
        expect(PLURALFUNCS['dsb'](1,false)).toEqual('one');
        expect(PLURALFUNCS['dsb'](2,false)).toEqual('two');
        expect(PLURALFUNCS['dsb'](4,false)).toEqual('few');
        expect(PLURALFUNCS['dsb'](11,false)).toEqual('other');
    });
    it( 'en', () => {
        expect(PLURALFUNCS['en'](1,true)).toEqual('one');
        expect(PLURALFUNCS['en'](1,true)).toEqual('one');
        expect(PLURALFUNCS['en'](2,true)).toEqual('two');
        expect(PLURALFUNCS['en'](3,true)).toEqual('few');
        expect(PLURALFUNCS['en'](4,true)).toEqual('other');
    });
    it( 'fa', () => {
        expect(PLURALFUNCS['fa'](1,true)).toEqual('other');
        expect(PLURALFUNCS['fa'](1,false)).toEqual('one');
        expect(PLURALFUNCS['fa'](2,false)).toEqual('other');
    });
    it( 'ff', () => {
        expect(PLURALFUNCS['ff'](1,true)).toEqual('other');
        expect(PLURALFUNCS['ff'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ff'](2,false)).toEqual('other');
    });
    it( 'fil', () => {
        expect(PLURALFUNCS['fil'](1,true)).toEqual('one');
        expect(PLURALFUNCS['fil'](2,true)).toEqual('other');
        expect(PLURALFUNCS['fil'](9.5,false)).toEqual('one');
        expect(PLURALFUNCS['fil'](9,false)).toEqual('other');
    });
    it( 'fr', () => {
        expect(PLURALFUNCS['fr'](1,true)).toEqual('one');
        expect(PLURALFUNCS['fr'](2,true)).toEqual('other');
        expect(PLURALFUNCS['fr'](1,false)).toEqual('one');
        expect(PLURALFUNCS['fr'](2,false)).toEqual('other');
    });
    it( 'ga', () => {
        expect(PLURALFUNCS['ga'](1,true)).toEqual('one');
        expect(PLURALFUNCS['ga'](2,true)).toEqual('other');
        expect(PLURALFUNCS['ga'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ga'](2,false)).toEqual('two');
        expect(PLURALFUNCS['ga'](6,false)).toEqual('few');
        expect(PLURALFUNCS['ga'](10,false)).toEqual('many');
        expect(PLURALFUNCS['ga'](11,false)).toEqual('other');
    });
    it( 'gd', () => {
        expect(PLURALFUNCS['gd'](11,true)).toEqual('one');
        expect(PLURALFUNCS['gd'](12,true)).toEqual('two');
        expect(PLURALFUNCS['gd'](13,true)).toEqual('few');
        expect(PLURALFUNCS['gd'](4,true)).toEqual('other');
        expect(PLURALFUNCS['gd'](11,false)).toEqual('one');
        expect(PLURALFUNCS['gd'](12,false)).toEqual('two');
        expect(PLURALFUNCS['gd'](19,false)).toEqual('few');
        expect(PLURALFUNCS['gd'](20,false)).toEqual('other');
    });
    it( 'gu', () => {
        expect(PLURALFUNCS['gu'](1,true)).toEqual('one');
        expect(PLURALFUNCS['gu'](3,true)).toEqual('two');
        expect(PLURALFUNCS['gu'](4,true)).toEqual('few');
        expect(PLURALFUNCS['gu'](6,true)).toEqual('many');
        expect(PLURALFUNCS['gu'](7,true)).toEqual('other');
        expect(PLURALFUNCS['gu'](0,false)).toEqual('one');
        expect(PLURALFUNCS['gu'](2,false)).toEqual('other'); 
    });
    it( 'gv', () => {
        expect(PLURALFUNCS['gv'](1,true)).toEqual('other');
        expect(PLURALFUNCS['gv'](1,false)).toEqual('one');
        expect(PLURALFUNCS['gv'](2,false)).toEqual('two');
        expect(PLURALFUNCS['gv'](80,false)).toEqual('few');
        expect(PLURALFUNCS['gv'](1.1,false)).toEqual('many'); 
        expect(PLURALFUNCS['gv'](90,false)).toEqual('other');
    });
    it( 'he', () => {
        expect(PLURALFUNCS['he'](1,true)).toEqual('other');
        expect(PLURALFUNCS['he'](1,false)).toEqual('one');
        expect(PLURALFUNCS['he'](2,false)).toEqual('two');
        expect(PLURALFUNCS['he'](100,false)).toEqual('many');
        expect(PLURALFUNCS['he'](111,false)).toEqual('other');
    });
    it( 'hi', () => {
        expect(PLURALFUNCS['hi'](1,true)).toEqual('one');
        expect(PLURALFUNCS['hi'](3,true)).toEqual('two');
        expect(PLURALFUNCS['hi'](4,true)).toEqual('few');
        expect(PLURALFUNCS['hi'](6,true)).toEqual('many');
        expect(PLURALFUNCS['hi'](7,true)).toEqual('other');
        expect(PLURALFUNCS['hi'](0,false)).toEqual('one');
        expect(PLURALFUNCS['hi'](2,false)).toEqual('other');
    });
    it( 'hr', () => {
        expect(PLURALFUNCS['hr'](1,true)).toEqual('other');
        expect(PLURALFUNCS['hr'](121,false)).toEqual('one');
        expect(PLURALFUNCS['hr'](1.101,false)).toEqual('one');
        expect(PLURALFUNCS['hr'](23,false)).toEqual('few');
        expect(PLURALFUNCS['hr'](1.23,false)).toEqual('few');
        expect(PLURALFUNCS['hr'](1.25,false)).toEqual('other');
    });
    it( 'hsb', () => {
        expect(PLURALFUNCS['hsb'](1,true)).toEqual('other');
        expect(PLURALFUNCS['hsb'](1,false)).toEqual('one');
        expect(PLURALFUNCS['hsb'](11.01,false)).toEqual('one');
        expect(PLURALFUNCS['hsb'](2,false)).toEqual('two');
        expect(PLURALFUNCS['hsb'](22.02,false)).toEqual('two');
        expect(PLURALFUNCS['hsb'](4,false)).toEqual('few');
        expect(PLURALFUNCS['hsb'](44.04,false)).toEqual('few');
        expect(PLURALFUNCS['hsb'](55.05,false)).toEqual('other');
    });
    it( 'hu', () => {
        expect(PLURALFUNCS['hu'](5,true)).toEqual('one');
        expect(PLURALFUNCS['hu'](2,true)).toEqual('other');
        expect(PLURALFUNCS['hu'](1,false)).toEqual('one');
        expect(PLURALFUNCS['hu'](2,false)).toEqual('other');
    });
    it( 'hy', () => {
        expect(PLURALFUNCS['hy'](1,true)).toEqual('one');
        expect(PLURALFUNCS['hy'](2,true)).toEqual('other');
        expect(PLURALFUNCS['hy'](0,false)).toEqual('one');
        expect(PLURALFUNCS['hy'](2,false)).toEqual('other');
    });
    it( 'is', () => {
        expect(PLURALFUNCS['is'](1,true)).toEqual('other');
        expect(PLURALFUNCS['is'](101,false)).toEqual('one');
        expect(PLURALFUNCS['is'](102,false)).toEqual('other');
    });
    it( 'it', () => {
        expect(PLURALFUNCS['it'](800,true)).toEqual('many');
        expect(PLURALFUNCS['it'](801,true)).toEqual('other');
        expect(PLURALFUNCS['it'](1,false)).toEqual('one');
        expect(PLURALFUNCS['it'](2,false)).toEqual('other');
    });
    it( 'iu', () => {
        expect(PLURALFUNCS['iu'](1,true)).toEqual('other');
        expect(PLURALFUNCS['iu'](1,false)).toEqual('one');
        expect(PLURALFUNCS['iu'](2,false)).toEqual('two');
        expect(PLURALFUNCS['iu'](3,false)).toEqual('other');
    });
    it( 'iw', () => {
        expect(PLURALFUNCS['iw'](1,true)).toEqual('other');
        expect(PLURALFUNCS['iw'](1,false)).toEqual('one');
        expect(PLURALFUNCS['iw'](2.0,false)).toEqual('two');
        expect(PLURALFUNCS['iw'](20.0,false)).toEqual('many');
        expect(PLURALFUNCS['iw'](3,false)).toEqual('other');
    });
    it( 'ka', () => {
        expect(PLURALFUNCS['ka'](1.9,true)).toEqual('one');
        expect(PLURALFUNCS['ka'](80.9,true)).toEqual('many');
        expect(PLURALFUNCS['ka'](90,true)).toEqual('other');
        expect(PLURALFUNCS['ka'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ka'](2,false)).toEqual('other');
    });
    it( 'kab', () => {
        expect(PLURALFUNCS['kab'](1.9,true)).toEqual('other');
        expect(PLURALFUNCS['kab'](0,false)).toEqual('one');
        expect(PLURALFUNCS['kab'](2,false)).toEqual('other');
    });
    it( 'kk', () => {
        expect(PLURALFUNCS['kk'](10,true)).toEqual('many');
        expect(PLURALFUNCS['kk'](11,true)).toEqual('other');
        expect(PLURALFUNCS['kk'](1,false)).toEqual('one');
        expect(PLURALFUNCS['kk'](2,false)).toEqual('other');
    });
    it( 'kn', () => {
        expect(PLURALFUNCS['kn'](1,true)).toEqual('other');
        expect(PLURALFUNCS['kn'](0,false)).toEqual('one');
        expect(PLURALFUNCS['kn'](2,false)).toEqual('other');
    });
    it( 'ksh', () => {
        expect(PLURALFUNCS['ksh'](1,true)).toEqual('other');
        expect(PLURALFUNCS['ksh'](0,false)).toEqual('zero');
        expect(PLURALFUNCS['ksh'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ksh'](2,false)).toEqual('other');
    });
    it( 'kw', () => {
        expect(PLURALFUNCS['kw'](1,true)).toEqual('other');
        expect(PLURALFUNCS['kw'](1,false)).toEqual('one');
        expect(PLURALFUNCS['kw'](2,false)).toEqual('two');
        expect(PLURALFUNCS['kw'](3,false)).toEqual('other');
    });
    it( 'lag', () => {
        expect(PLURALFUNCS['lag'](1,true)).toEqual('other');
        expect(PLURALFUNCS['lag'](0,false)).toEqual('zero');
        expect(PLURALFUNCS['lag'](1.5,false)).toEqual('one');
        expect(PLURALFUNCS['lag'](3,false)).toEqual('other');
    });
    it( 'lo', () => {
        expect(PLURALFUNCS['lo'](1,true)).toEqual('one');
        expect(PLURALFUNCS['lo'](1.5,true)).toEqual('other');
        expect(PLURALFUNCS['lo'](0,false)).toEqual('other');
    });
    it( 'lt', () => {
        expect(PLURALFUNCS['lt'](1.5,true)).toEqual('other');
        expect(PLURALFUNCS['lt'](21,false)).toEqual('one');
        expect(PLURALFUNCS['lt'](28,false)).toEqual('few');
        expect(PLURALFUNCS['lt'](1.5,false)).toEqual('many');
        expect(PLURALFUNCS['lt'](200,false)).toEqual('other');
    });
    it( 'lv', () => {
        expect(PLURALFUNCS['lv'](1.5,true)).toEqual('other');
        expect(PLURALFUNCS['lv'](20.18,false)).toEqual('zero');
        expect(PLURALFUNCS['lv'](21,false)).toEqual('one');
        expect(PLURALFUNCS['lv'](11.21,false)).toEqual('one');
        expect(PLURALFUNCS['lv'](25,false)).toEqual('other');
    });
    it( 'mk', () => {
        expect(PLURALFUNCS['mk'](21,true)).toEqual('one');
        expect(PLURALFUNCS['mk'](22,true)).toEqual('two');
        expect(PLURALFUNCS['mk'](28,true)).toEqual('many');
        expect(PLURALFUNCS['mk'](29,true)).toEqual('other');
        expect(PLURALFUNCS['mk'](21,false)).toEqual('one');
        expect(PLURALFUNCS['mk'](1.21,false)).toEqual('one');
        expect(PLURALFUNCS['mk'](1.11,false)).toEqual('other');
    });
    it( 'mo', () => {
        expect(PLURALFUNCS['mo'](1,true)).toEqual('one');
        expect(PLURALFUNCS['mo'](2,true)).toEqual('other');
        expect(PLURALFUNCS['mo'](1,false)).toEqual('one');
        expect(PLURALFUNCS['mo'](18,false)).toEqual('few');
        expect(PLURALFUNCS['mo'](20,false)).toEqual('other');
    });
    it( 'mr', () => {
        expect(PLURALFUNCS['mr'](1,true)).toEqual('one');
        expect(PLURALFUNCS['mr'](3,true)).toEqual('two');
        expect(PLURALFUNCS['mr'](4,true)).toEqual('few');
        expect(PLURALFUNCS['mr'](5,true)).toEqual('other');
        expect(PLURALFUNCS['mr'](0,false)).toEqual('one');
        expect(PLURALFUNCS['mr'](2,false)).toEqual('other');
    });
    it( 'ms', () => {
        expect(PLURALFUNCS['ms'](1,true)).toEqual('one');
        expect(PLURALFUNCS['ms'](2,true)).toEqual('other');
        expect(PLURALFUNCS['ms'](1,false)).toEqual('other');
    });
    it( 'mt', () => {
        expect(PLURALFUNCS['mt'](1,true)).toEqual('other');
        expect(PLURALFUNCS['mt'](1,false)).toEqual('one');
        expect(PLURALFUNCS['mt'](10,false)).toEqual('few');
        expect(PLURALFUNCS['mt'](19,false)).toEqual('many');
        expect(PLURALFUNCS['mt'](20,false)).toEqual('other');
    });
    it( 'naq', () => {
        expect(PLURALFUNCS['naq'](1,true)).toEqual('other');
        expect(PLURALFUNCS['naq'](1,false)).toEqual('one');
        expect(PLURALFUNCS['naq'](2,false)).toEqual('two');
        expect(PLURALFUNCS['naq'](3,false)).toEqual('other');
    });
    it( 'ne', () => {
        expect(PLURALFUNCS['ne'](4,true)).toEqual('one');
        expect(PLURALFUNCS['ne'](5,true)).toEqual('other');
        expect(PLURALFUNCS['ne'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ne'](2,false)).toEqual('other');
    });
    it( 'or', () => {
        expect(PLURALFUNCS['or'](8,true)).toEqual('one');
        expect(PLURALFUNCS['or'](3,true)).toEqual('two');
        expect(PLURALFUNCS['or'](4,true)).toEqual('few');
        expect(PLURALFUNCS['or'](6,true)).toEqual('many');
        expect(PLURALFUNCS['or'](10,true)).toEqual('other');
        expect(PLURALFUNCS['or'](1,false)).toEqual('one');
        expect(PLURALFUNCS['or'](2,false)).toEqual('other');
    });
    it( 'pl', () => {
        expect(PLURALFUNCS['pl'](1.23,true)).toEqual('other');
        expect(PLURALFUNCS['pl'](1,false)).toEqual('one');
        expect(PLURALFUNCS['pl'](22,false)).toEqual('few');
        expect(PLURALFUNCS['pl'](113,false)).toEqual('many');
        expect(PLURALFUNCS['pl'](115,false)).toEqual('many');
        expect(PLURALFUNCS['pl'](1.23,false)).toEqual('other');
    });
    it( 'prg', () => {
        expect(PLURALFUNCS['prg'](1.23,true)).toEqual('other');
        expect(PLURALFUNCS['prg'](0.15,false)).toEqual('zero');
        expect(PLURALFUNCS['prg'](21,false)).toEqual('one');
        expect(PLURALFUNCS['prg'](0.21,false)).toEqual('one');
        expect(PLURALFUNCS['prg'](55,false)).toEqual('other');
    });
    it( 'pt', () => {
        expect(PLURALFUNCS['pt'](1.23,true)).toEqual('other');
        expect(PLURALFUNCS['pt'](1.0,false)).toEqual('one');
        expect(PLURALFUNCS['pt'](2.0,false)).toEqual('other');
    });

    it( 'ro', () => {
        expect(PLURALFUNCS['ro'](1,true)).toEqual('one');
        expect(PLURALFUNCS['ro'](2,true)).toEqual('other');
        expect(PLURALFUNCS['ro'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ro'](19,false)).toEqual('few');
        expect(PLURALFUNCS['ro'](20,false)).toEqual('other');
    });
    it( 'ru', () => {
        expect(PLURALFUNCS['ru'](1,true)).toEqual('other'); 
        expect(PLURALFUNCS['ru'](1,false)).toEqual('one');
        expect(PLURALFUNCS['ru'](23,false)).toEqual('few');
        expect(PLURALFUNCS['ru'](14,false)).toEqual('many');
        expect(PLURALFUNCS['ru'](19,false)).toEqual('many');
        expect(PLURALFUNCS['ru'](101.1,false)).toEqual('other');
    });
    it( 'sc', () => {
        expect(PLURALFUNCS['sc'](800,true)).toEqual('many');
        expect(PLURALFUNCS['sc'](801,true)).toEqual('other');
        expect(PLURALFUNCS['sc'](1,false)).toEqual('one');
        expect(PLURALFUNCS['sc'](2,false)).toEqual('other');
    });
    it( 'scn', () => {
        expect(PLURALFUNCS['scn'](800,true)).toEqual('many');
        expect(PLURALFUNCS['scn'](801,true)).toEqual('other');
        expect(PLURALFUNCS['scn'](1,false)).toEqual('one');
        expect(PLURALFUNCS['scn'](2,false)).toEqual('other');
    });
    it( 'se', () => {
        expect(PLURALFUNCS['se'](1,true)).toEqual('other');
        expect(PLURALFUNCS['se'](1,false)).toEqual('one');
        expect(PLURALFUNCS['se'](2,false)).toEqual('two');
        expect(PLURALFUNCS['se'](3,false)).toEqual('other');
    });
    it( 'sh', () => {
        expect(PLURALFUNCS['sh'](1,true)).toEqual('other');
        expect(PLURALFUNCS['sh'](101,false)).toEqual('one');
        expect(PLURALFUNCS['sh'](1.101,false)).toEqual('one');
        expect(PLURALFUNCS['sh'](23,false)).toEqual('few');
        expect(PLURALFUNCS['sh'](1.23,false)).toEqual('few');
        expect(PLURALFUNCS['sh'](1.15,false)).toEqual('other');
    });
    it( 'shi', () => {
        expect(PLURALFUNCS['shi'](1,true)).toEqual('other');
        expect(PLURALFUNCS['shi'](1,false)).toEqual('one');
        expect(PLURALFUNCS['shi'](2,false)).toEqual('few');
        expect(PLURALFUNCS['shi'](11,false)).toEqual('other');
    });
    it( 'si', () => {
        expect(PLURALFUNCS['si'](1,true)).toEqual('other');
        expect(PLURALFUNCS['si'](1,false)).toEqual('one');
        expect(PLURALFUNCS['si'](0.1,false)).toEqual('one');
        expect(PLURALFUNCS['si'](2,false)).toEqual('other');
    });
    it( 'sk', () => {
        expect(PLURALFUNCS['sk'](1,true)).toEqual('other');
        expect(PLURALFUNCS['sk'](1,false)).toEqual('one');
        expect(PLURALFUNCS['sk'](2,false)).toEqual('few');
        expect(PLURALFUNCS['sk'](5.5,false)).toEqual('many');
        expect(PLURALFUNCS['sk'](5,false)).toEqual('other');
    });
    it( 'sl', () => {
        expect(PLURALFUNCS['sl'](1,true)).toEqual('other');
        expect(PLURALFUNCS['sl'](101,false)).toEqual('one');
        expect(PLURALFUNCS['sl'](102,false)).toEqual('two');
        expect(PLURALFUNCS['sl'](104,false)).toEqual('few');
        expect(PLURALFUNCS['sl'](104.5,false)).toEqual('few');
        expect(PLURALFUNCS['sl'](105,false)).toEqual('other');
    });
    it( 'sma', () => {
        expect(PLURALFUNCS['sma'](1,true)).toEqual('other');
        expect(PLURALFUNCS['sma'](1,false)).toEqual('one');
        expect(PLURALFUNCS['sma'](2,false)).toEqual('two');
        expect(PLURALFUNCS['sma'](3,false)).toEqual('other');
    });
    it( 'smi', () => {
        expect(PLURALFUNCS['smi'](1,true)).toEqual('other');
        expect(PLURALFUNCS['smi'](1,false)).toEqual('one');
        expect(PLURALFUNCS['smi'](2,false)).toEqual('two');
        expect(PLURALFUNCS['smi'](3,false)).toEqual('other');
    });
    it( 'smj', () => {
        expect(PLURALFUNCS['smj'](1,true)).toEqual('other');
        expect(PLURALFUNCS['smj'](1,false)).toEqual('one');
        expect(PLURALFUNCS['smj'](2,false)).toEqual('two');
        expect(PLURALFUNCS['smj'](3,false)).toEqual('other');
    });
    it( 'smn', () => {
        expect(PLURALFUNCS['smn'](1,true)).toEqual('other');
        expect(PLURALFUNCS['smn'](1,false)).toEqual('one');
        expect(PLURALFUNCS['smn'](2,false)).toEqual('two');
        expect(PLURALFUNCS['smn'](3,false)).toEqual('other');
    });
    it( 'sms', () => {
        expect(PLURALFUNCS['sms'](1,true)).toEqual('other');
        expect(PLURALFUNCS['sms'](1,false)).toEqual('one');
        expect(PLURALFUNCS['sms'](2,false)).toEqual('two');
        expect(PLURALFUNCS['sms'](3,false)).toEqual('other');
    });
    it( 'sq', () => {
        expect(PLURALFUNCS['sq'](1,true)).toEqual('one');
        expect(PLURALFUNCS['sq'](24,true)).toEqual('many');
        expect(PLURALFUNCS['sq'](25,true)).toEqual('other');
        expect(PLURALFUNCS['sq'](25,false)).toEqual('other');
        expect(PLURALFUNCS['sq'](1,false)).toEqual('one');
        expect(PLURALFUNCS['sq'](2,false)).toEqual('other');
    });
    it( 'sr', () => {
        expect(PLURALFUNCS['sr'](1,true)).toEqual('other');
        expect(PLURALFUNCS['sr'](101,false)).toEqual('one');
        expect(PLURALFUNCS['sr'](1.101,false)).toEqual('one');
        expect(PLURALFUNCS['sr'](23,false)).toEqual('few');
        expect(PLURALFUNCS['sr'](1.23,false)).toEqual('few');
        expect(PLURALFUNCS['sr'](25,false)).toEqual('other');
    });
    it( 'sv', () => {
        expect(PLURALFUNCS['sv'](102,true)).toEqual('one');
        expect(PLURALFUNCS['sv'](103,true)).toEqual('other');
        expect(PLURALFUNCS['sv'](1,false)).toEqual('one');
        expect(PLURALFUNCS['sv'](2,false)).toEqual('other');
    });
    it( 'tk', () => {
        expect(PLURALFUNCS['tk'](10,true)).toEqual('few');
        expect(PLURALFUNCS['tk'](1,true)).toEqual('other');
        expect(PLURALFUNCS['tk'](1,false)).toEqual('one');
        expect(PLURALFUNCS['tk'](2,false)).toEqual('other');
    });
    it( 'tl', () => {
        expect(PLURALFUNCS['tl'](1,true)).toEqual('one');
        expect(PLURALFUNCS['tl'](2,true)).toEqual('other');
        expect(PLURALFUNCS['tl'](5,false)).toEqual('one');
        expect(PLURALFUNCS['tl'](5.5,false)).toEqual('one');
        expect(PLURALFUNCS['tl'](9,false)).toEqual('other');
    });
    it( 'tzm', () => {
        expect(PLURALFUNCS['tzm'](1,true)).toEqual('other');
        expect(PLURALFUNCS['tzm'](11,false)).toEqual('one');
        expect(PLURALFUNCS['tzm'](100,false)).toEqual('other');
    });
    it( 'uk', () => {
        expect(PLURALFUNCS['uk'](23,true)).toEqual('few');
        expect(PLURALFUNCS['uk'](13,true)).toEqual('other');
        expect(PLURALFUNCS['uk'](21,false)).toEqual('one');
        expect(PLURALFUNCS['uk'](24,false)).toEqual('few');
        expect(PLURALFUNCS['uk'](114,false)).toEqual('many');
        expect(PLURALFUNCS['uk'](119,false)).toEqual('many');
        expect(PLURALFUNCS['uk'](114.5,false)).toEqual('other');
    });
    it( 'vi', () => {
        expect(PLURALFUNCS['vi'](1,true)).toEqual('one');
        expect(PLURALFUNCS['vi'](2,true)).toEqual('other');
        expect(PLURALFUNCS['vi'](1,false)).toEqual('other');
    });
    it( 'zu', () => {
        expect(PLURALFUNCS['zu'](1,true)).toEqual('other');
        expect(PLURALFUNCS['zu'](0,false)).toEqual('one');
        expect(PLURALFUNCS['zu'](2,false)).toEqual('other');
    });
});