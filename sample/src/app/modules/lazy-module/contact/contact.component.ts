import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'contact',
    templateUrl: './contact.component.html',
    styleUrls: ['./contact.component.css']
})
export class CotactComponent implements OnInit {
    contributors: Object[];
    constructor() { }

    ngOnInit() {
        this.contributors = [
            {
                abbreviations: 'RP',
                name: 'Raymond Peng',
                email: 'anonrongbo@gmail.com'
            }
        ];
    }

}
