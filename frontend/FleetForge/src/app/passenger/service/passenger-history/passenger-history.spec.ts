import { TestBed } from '@angular/core/testing';

import { PassengerHistory } from './passenger-history';

describe('PassengerHistory', () => {
  let service: PassengerHistory;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PassengerHistory);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
