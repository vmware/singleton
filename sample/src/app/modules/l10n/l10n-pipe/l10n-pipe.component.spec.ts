import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { L10nPipeComponent } from './l10n-pipe.component';

describe('L10nPipeComponent', () => {
  let component: L10nPipeComponent;
  let fixture: ComponentFixture<L10nPipeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ L10nPipeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(L10nPipeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
