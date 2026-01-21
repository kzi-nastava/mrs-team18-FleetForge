import { TestBed } from '@angular/core/testing';

import { Navbar } from './navbar';

describe('Navbar', () => {
  let service: Navbar;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Navbar);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
