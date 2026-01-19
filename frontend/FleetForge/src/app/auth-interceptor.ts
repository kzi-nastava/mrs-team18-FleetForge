import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth/service/auth.service';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  // Clone request and add authorization header if token exists
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  // Handle response and catch 401 errors
  return next(req).pipe(
    catchError((error) => {
      if (error.status === 401) {
        // Token expired or invalid - logout user
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};