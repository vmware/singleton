/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { VIPModule, VIPService } from '../index';
import { Injector } from '@angular/core';
// tslint:disable-next-line: import-blacklist
import { getTestBed, TestBed, } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';

describe( 'VIPService', () => {
    let injector: Injector;
    let vipService: VIPService;
    // function initVIPConfig( service: VIPService ) {
    //     return () => service.initData();
    // }
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports : [
                HttpClientModule,
                VIPModule.forRoot()
            ]
        });
        injector = getTestBed();
        vipService = injector.get(VIPService);
    });

    it('should defined', () => {
        expect(VIPService).toBeDefined();
        expect(vipService).toBeDefined();
        expect( vipService instanceof VIPService).toBeTruthy();
    });
});
