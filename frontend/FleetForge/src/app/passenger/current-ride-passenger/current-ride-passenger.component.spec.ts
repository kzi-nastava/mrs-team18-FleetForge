import { TestBed } from '@angular/core/testing';
import { CurrentRidePassengerComponent } from './current-ride-passenger.component';

describe('CurrentRidePassengerComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CurrentRidePassengerComponent]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(CurrentRidePassengerComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
