import { TestBed } from '@angular/core/testing';

import { DriverProfileChangesService } from './driver-profile-changes-service';

describe('DriverProfileChangesService', () => {
  let service: DriverProfileChangesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverProfileChangesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
