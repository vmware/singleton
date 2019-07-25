import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'percent',
  templateUrl: './percent.component.html',
  styleUrls: ['./percent.component.css']
})
export class PercentComponent implements OnInit {

  numbers: number[];
    constructor() {}

    ngOnInit() {
        this.numbers = [
            0.1,
            0.23,
            0.501,
            0.5569,
            123.45
        ];
    }

}
