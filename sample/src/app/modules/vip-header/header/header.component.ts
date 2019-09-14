import { Component, OnInit, Input } from '@angular/core';
import { I18nService, VIPService, LocaleService } from '@singleton-i18n/angular-client';


@Component({
    selector: 'vip-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
    public options: any;
    public languages: any[];
    public regions: any;
    public selectedLanguage: string;
    public selectedRegion: string;
    showDrawer: boolean;
    constructor(private i18nService: I18nService, private vipService: VIPService, private localeService: LocaleService) { }

    ngOnInit() {
        this.selectedLanguage = this.localeService.getCurrentLanguage();
        this.selectedRegion = this.localeService.getCurrentRegion();

        this.getLanguages();
        this.getRegions(this.selectedLanguage);
        this.options = {
            baseRoute: '/l10n',
            navs: [
                {
                    route: 'l10n',
                    key: 'l10n'
                },
                {
                    route: 'i18n',
                    key: 'i18n'
                },
                {
                    route: 'contact',
                    key: 'contact'
                }
            ],
            title: 'NGX Singleton Sample',
            languages: this.languages,
            regions: this.regions
        };
    }
    switchDrawer() {
        this.showDrawer = true;
    }
    hideDrawer() {
        this.showDrawer = false;
    }
    setLanguage(language: string) {
        this.localeService.setCurrentLanguage(language);
        this.selectedLanguage = this.localeService.getCurrentLanguage();
        this.getRegions( this.selectedLanguage );
    }
    setRegion(newRegion: string) {
        this.localeService.setCurrentRegion(newRegion);
        this.selectedRegion = this.localeService.getCurrentRegion();
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
