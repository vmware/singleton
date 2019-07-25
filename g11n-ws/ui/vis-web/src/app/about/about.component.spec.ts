/*
* Copyright 2019 VMware, Inc.
* SPDX-License-Identifier: EPL-2.0
*/
import { async, TestBed, ComponentFixture } from "@angular/core/testing";
import { ClarityModule } from '@clr/angular';
import { AboutComponent } from './about.component';


describe('AboutComponent', () => {

    let expectedMsg: string = 'This is a page to help demonstrate routing.';

    let fixture: ComponentFixture<any>;
    let compiled: any;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [
                AboutComponent
            ],
            imports: [
                ClarityModule.forRoot()
            ]
        });

        fixture = TestBed.createComponent(AboutComponent);
        fixture.detectChanges();
        compiled = fixture.nativeElement;

    });

    afterEach(() => {
        fixture.destroy();
    });

    it('should create the about page', async(() => {
        expect(compiled).toBeTruthy();
    }));

    it(`should display: "${expectedMsg}"`, async(() => {
        expect(compiled.querySelector("p").textContent).toMatch(expectedMsg);
    }));


});
