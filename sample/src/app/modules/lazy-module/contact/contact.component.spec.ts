import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CotactComponent } from './contact.component';

describe('CotactComponent', () => {
  let component: CotactComponent;
  let fixture: ComponentFixture<CotactComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CotactComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CotactComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
