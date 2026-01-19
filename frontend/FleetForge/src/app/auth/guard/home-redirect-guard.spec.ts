import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { homeRedirectGuard } from './home-redirect-guard';

describe('homeRedirectGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => homeRedirectGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
