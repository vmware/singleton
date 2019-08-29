import { Component, OnInit } from '@angular/core';
import { I18nService, VIPService, LocaleService } from '@singleton-i18n/angular-client';



@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
    title = 'NgxVIP-Test';
    public selectedLanguage: string;
    public selectedRegion: string;
    public languages: any[];
    public regions: any;

    constructor(private i18nService: I18nService, private vipService: VIPService, private localeService: LocaleService) {
    }
    setLanguage(language: string) {
        // localStorage.setItem('language', language);
        this.localeService.setCurrentLanguage(language);
    }
    setRegion(newRegion: string) {
        // localStorage.setItem('region', newRegion);
        this.localeService.setCurrentRegion(newRegion);
    }
    ngOnInit() {
        this.selectedLanguage = this.localeService.getCurrentLanguage();
        this.selectedRegion = this.localeService.getCurrentRegion();
    }
}
