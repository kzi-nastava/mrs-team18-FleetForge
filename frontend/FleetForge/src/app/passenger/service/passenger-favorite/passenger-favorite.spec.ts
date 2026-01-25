import { TestBed } from '@angular/core/testing';

import { PassengerFavorite } from './passenger-favorite';

describe('PassengerFavorite', () => {
  let service: PassengerFavorite;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PassengerFavorite);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
