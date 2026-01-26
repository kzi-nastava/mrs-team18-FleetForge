import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerScheduledRidesComponent } from './passenger-scheduled-rides.component';

describe('PassengerScheduledRidesComponent', () => {
  let component: PassengerScheduledRidesComponent;
  let fixture: ComponentFixture<PassengerScheduledRidesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassengerScheduledRidesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerScheduledRidesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
