import { TestBed } from '@angular/core/testing';
import { PassengerCurrentRide } from './passenger-current-ride';

describe('PassengerCurrentRide', () => {
  let service: PassengerCurrentRide;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PassengerCurrentRide);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
