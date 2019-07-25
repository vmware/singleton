import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { I18nServiceComponent } from './i18n-service.component';

describe('I18nServiceComponent', () => {
  let component: I18nServiceComponent;
  let fixture: ComponentFixture<I18nServiceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ I18nServiceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(I18nServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
