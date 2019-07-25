import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { L10nComponent } from './l10n.component';

describe('L10nComponent', () => {
  let component: L10nComponent;
  let fixture: ComponentFixture<L10nComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ L10nComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(L10nComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
