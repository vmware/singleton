import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'number',
  templateUrl: './number.component.html',
  styleUrls: ['./number.component.css']
})
export class NumberComponent implements OnInit {

    numbers: number[];
    constructor() {}

    ngOnInit() {
        this.numbers = [
            0.1,
            0.23,
            1.2345,
            12345
        ];
    }

}
