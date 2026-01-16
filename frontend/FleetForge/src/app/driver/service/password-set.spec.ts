import { TestBed } from '@angular/core/testing';

import { PasswordSet } from './password-set';

describe('PasswordSet', () => {
  let service: PasswordSet;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PasswordSet);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
