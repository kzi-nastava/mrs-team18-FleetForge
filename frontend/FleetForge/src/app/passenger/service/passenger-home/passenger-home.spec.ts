import { TestBed } from '@angular/core/testing';

import { PassengerHome } from './passenger-home';

describe('PassengerHome', () => {
  let service: PassengerHome;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PassengerHome);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
