/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { VIPModule, VIPService, getNameSpace, LocaleService } from '../index';
import { Injectable, ViewContainerRef, Component, ViewChild, ElementRef, APP_INITIALIZER, Injector } from '@angular/core';
import { TestBed, ComponentFixture, async, getTestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { TestLoader, baseConfig } from './test.util';
import { I18nLoader } from '../src/loader';


@Injectable()
@Component({
    selector: 'app-l10n',
    template: `
            <div style="text-align:center">
                <h2 #origin [l10n]="'vm.test'" [source]="'test string'"></h2>
                <h2 #withParam [l10n]="'vm.parse'" [source]="'No custom buckets specified for {0}'" [params]="['The product']"></h2>
                <h2 #withParams  [l10n]="'vm.params'" [source]="'Key = {0}, Status = {1}, Type = {2}, Quantity = {3}'"
                    [params]="['afe9ftsxdsafd48f4dsa6','enabled','$','100']"></h2>
                <h2 #withNoTranslation [l10n]="'vm.no.translate'" [source]="'No translation for {0}'" [params]="['this one']"></h2>
            </div>`
})
class TestComponent {
    viewContainerRef: ViewContainerRef;
    @ViewChild('origin', { static: false } ) origin: ElementRef;
    @ViewChild('withParam', { static: false } ) withParam: ElementRef;
    @ViewChild('withParams', { static: false } ) withParams: ElementRef;
    @ViewChild('withNoTranslation', { static: false } ) withNoTranslation: ElementRef;
    constructor(viewContainerRef: ViewContainerRef) {
        this.viewContainerRef = viewContainerRef;
    }
}

describe('l10n directive', () => {
    let locale = 'en';

    const fr = {
        'vm.test': 'Caractères d’essai',
        'vm.parse': 'Aucun compartiment personnalisé spécifié pour The product',
        'vm.params': 'Clé = {0}, État = {1}, Type = {2}, Quantité = {3}'
    };
    const en = {
        'vm.test': 'test string',
        'vm.parse': 'No custom buckets specified for {0}',
        'vm.params': 'Key = {0}, Status = {1}, Type = {2}, Quantity = {3}',
        'vm.no.translate': 'No translation for {0}'
    };
    /**
     * Mock loader for testing purpose.
     * @class VIPRestLoader
     * @implements {VIPLoader}
     */
    const config = Object.assign({}, baseConfig, { sourceBundles: [en], isPseudo: true });

    @Injectable()
    class VIPRestLoader extends TestLoader {
        getLocaleData(): Observable<any> {
            const namespace = getNameSpace(config);
            const translations: { [key: string]: any } = {};
            translations[namespace] = fr;
            const response = of({ messages: translations });
            return response;
        }
    }

    let fixture: ComponentFixture < TestComponent > ;
    let testComponent: TestComponent;

    function initVIPConfig(service: VIPService) {
        return () => service.initData(config);
    }

    function createComponent() {
        TestBed.configureTestingModule({
            imports: [
                VIPModule.forRoot({
                    coreLoader: {
                        provide: I18nLoader,
                        useClass: VIPRestLoader
                    }
                })
            ],
            providers: [
                {
                    provide: APP_INITIALIZER,
                    useFactory: initVIPConfig,
                    deps: [VIPService],
                    multi: true
                }
            ],
            declarations: [TestComponent]
        });

        fixture = TestBed.createComponent(TestComponent);
        if (locale === 'fr') {
            const injector: Injector = getTestBed();
            const localeService = injector.get(LocaleService);
            localeService.setCurrentLocale(locale);
        }
        testComponent = fixture.componentInstance;
    }

    describe('when locale is en', () => {
        beforeEach(() => {
            locale = 'en';
            createComponent();
        });

        it('should translate with source', async(() => {

            fixture.whenStable().then(() => {

                fixture.detectChanges();

                expect(testComponent.origin.nativeElement.innerHTML).toEqual('test string');

                expect(testComponent.withParam.nativeElement.innerHTML)
                    .toEqual('No custom buckets specified for The product');

                expect(testComponent.withParams.nativeElement.innerHTML)
                    .toEqual('Key = afe9ftsxdsafd48f4dsa6, Status = enabled, Type = $, Quantity = 100');

                expect(testComponent.withNoTranslation.nativeElement.innerHTML)
                    .toEqual('No translation for this one');

            });
        }));
    });

    describe('when locale is fr', () => {
        beforeEach(() => {
            locale = 'fr';
            createComponent();
        });

        it('should translate with translation', async(() => {
            fixture.whenStable().then(() => {

                fixture.detectChanges();

                expect(testComponent.origin.nativeElement.innerHTML).toEqual('Caractères d’essai');

                expect(testComponent.withParam.nativeElement.innerHTML)
                    .toEqual('Aucun compartiment personnalisé spécifié pour The product');

                expect(testComponent.withParams.nativeElement.innerHTML)
                    .toEqual('Clé = afe9ftsxdsafd48f4dsa6, État = enabled, Type = $, Quantité = 100');

                expect(testComponent.withNoTranslation.nativeElement.innerHTML)
                    .toEqual('@@No translation for this one@@');

            });
        }));
    });
});
