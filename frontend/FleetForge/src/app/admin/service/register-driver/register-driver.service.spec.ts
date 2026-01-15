import { TestBed } from '@angular/core/testing';

import { RegisterDriverService } from './register-driver.service';

describe('RegisterDriverService', () => {
  let service: RegisterDriverService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RegisterDriverService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
