import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'currency',
    templateUrl: './currency.component.html',
    styleUrls: ['./currency.component.css']
})
export class CurrencyComponent implements OnInit {
    datas: Object[];

    constructor() { }

    ngOnInit() {
        this.datas = [
            {
                number: 1.2,
                code: 'USD'
            },
            {
                number: 1.2345,
                code: 'EUR'
            },
            {
                number: 9.82501,
                code: 'JPY'
            }
        ];
    }

}
