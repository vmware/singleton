import { Component, OnInit } from '@angular/core';
import { I18nService } from '@singleton-i18n/angular-client';


@Component({
    selector: 'i18n-service',
    templateUrl: './i18n-service.component.html',
    styleUrls: ['./i18n-service.component.css']
})
export class I18nServiceComponent implements OnInit {
    public languages: any[];
    public regions: any;
    public selectedLanguage: string;
    public selectedRegion: string;

    constructor(private i18nService: I18nService) { }

    ngOnInit() {
        this.selectedLanguage = 'en';
        this.selectedRegion = 'US';

        this.getLanguages();
        this.getRegions(this.selectedLanguage);
    }
    setLanguage( language: string ) {
        this.getRegions( language );
    }

    getLanguages() {
        this.i18nService.getSupportedLanguages().then(
            (languages: any) => {
                this.languages = languages;
            }
        );
    }

    getRegions(language: string) {
        this.i18nService.getSupportedRegions(language).then(
            (res: any) => {
                if (res) {
                    this.regions = res;
                }
            }
        );
    }
}
