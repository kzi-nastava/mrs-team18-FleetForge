import { TestBed } from '@angular/core/testing';
import { DriverCurrentRide } from './driver-current-ride';

describe('DriverCurrentRide', () => {
  let service: DriverCurrentRide;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverCurrentRide);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
