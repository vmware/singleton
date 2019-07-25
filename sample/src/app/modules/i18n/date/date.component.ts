import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'date',
  templateUrl: './date.component.html',
  styleUrls: ['./date.component.css']
})
export class DateComponent implements OnInit {
  time: Date;
  constructor() { }

  ngOnInit() {
    this.time = new Date();
  }

}
