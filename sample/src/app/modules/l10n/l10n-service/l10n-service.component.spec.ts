import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { L10nServiceComponent } from './l10n-service.component';

describe('L10nServiceComponent', () => {
  let component: L10nServiceComponent;
  let fixture: ComponentFixture<L10nServiceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ L10nServiceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(L10nServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
