import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../service/auth.service';

export const homeRedirectGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return true;
  }

  const role = authService.getRole();

  if (role === 'PASSENGER') {
    router.navigate(['/passenger/passenger-home']);
    return false;
  }

  if (role === 'DRIVER') {
    router.navigate(['/driver/dashboard']);
    return false;
  }

  return true;
};
