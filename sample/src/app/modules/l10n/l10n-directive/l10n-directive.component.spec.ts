import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { L10nDirectiveComponent } from './l10n-directive.component';

describe('L10nDirectiveComponent', () => {
  let component: L10nDirectiveComponent;
  let fixture: ComponentFixture<L10nDirectiveComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ L10nDirectiveComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(L10nDirectiveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
