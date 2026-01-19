import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../../auth/service/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }
  
  // Redirect to login and pass the attempted URL
  router.navigate(['/login'], { 
    queryParams: { returnUrl: state.url } 
  });
  return false;
};

export const guestGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return true;
  }
  // If passenger navigate to passenger home
  const role = authService.getRole();
  if (role === 'PASSENGER') {
    router.navigate(['/passenger/passenger-home']);
    return false;
  }
  // Already logged in, redirect to home
  router.navigate(['/']);
  return false;
};

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const role = authService.getRole();
    
    if (role && allowedRoles.includes(role)) {
      return true;
    }
    
    // User doesn't have required role, redirect to home
    router.navigate(['/']);
    return false;
  };
};