import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupDialogComponent } from './notification-popup.component';

describe('PopupDialogComponent', () => {
  let component: PopupDialogComponent;
  let fixture: ComponentFixture<PopupDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PopupDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
