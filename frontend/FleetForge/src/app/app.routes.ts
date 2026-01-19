import { Routes } from '@angular/router';

import { MainLayoutComponent } from './layout/main/main.layout';
import { AuthLayoutComponent } from './layout/auth/auth.layout';

import { HomeComponent } from './home/home.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ResetPasswordComponent } from './auth/reset-password/reset-password.component';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password.component';
import { DriverProfileComponent } from './profiles/driver-profile/driver-profile.component';
import { PassengerProfileComponent } from './profiles/passenger-profile/passenger-profile.component';
import { PasswordResetComponent } from './profiles/password-reset/password-reset.component';
import { DriverHistoryComponent } from './driver/driver-history/driver-history.component';
import { AdminProfileComponent } from './profiles/admin-profile/admin-profile.component';
import { DriverProfileChangesComponent } from './admin/driver-profile-changes/driver-profile-changes.component';
import { PasswordSetComponent } from './driver/password-set/password-set.component';
import { RegisterDriverComponent } from './admin/register-driver/register-driver.component';
import { ActivateAccountComponent } from './auth/activate-account/activate-account.component';
import { PassengerHomeComponent } from './passenger/passenger-home/passenger-home.component';

import { authGuard, guestGuard, roleGuard } from './auth/guard/auth-guard';
import { homeRedirectGuard } from './auth/guard/home-redirect-guard';

export const routes: Routes = [
  /** ROUTES WITH NAVBAR */
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      // Home page - accessible by everyone (no guard)            
      {
        path: '',
        component: HomeComponent,
        canActivate: [homeRedirectGuard]
      },

      
      // Passenger routes - require authentication and PASSENGER role
      { 
        path: 'profile-passenger', 
        component: PassengerProfileComponent,
        canActivate: [authGuard, roleGuard(['PASSENGER'])]
      },
      { 
        path: 'passenger/passenger-home', 
        component: PassengerHomeComponent,
        canActivate: [authGuard, roleGuard(['PASSENGER'])]
      },
      
      // Admin routes - require authentication and ADMIN role
      { 
        path: 'profile-admin', 
        component: AdminProfileComponent,
        canActivate: [authGuard, roleGuard(['ADMIN'])]
      },
      { 
        path: 'admin/driver-changes', 
        component: DriverProfileChangesComponent,
        canActivate: [authGuard, roleGuard(['ADMIN'])]
      },
      { 
        path: 'admin/register-new-driver', 
        component: RegisterDriverComponent,
        canActivate: [authGuard, roleGuard(['ADMIN'])]
      },
      
      // Driver routes - require authentication and DRIVER role
      { 
        path: 'profile-driver', 
        component: DriverProfileComponent,
        canActivate: [authGuard, roleGuard(['DRIVER'])]
      },
      { 
        path: 'driver/ride-history', 
        component: DriverHistoryComponent,
        canActivate: [authGuard, roleGuard(['DRIVER'])]
      },
      { 
        path: 'set-password', 
        component: PasswordSetComponent,
        canActivate: [authGuard, roleGuard(['DRIVER'])]
      },
      
      // Password reset - accessible by all authenticated users
      { 
        path: 'profile-password-reset', 
        component: PasswordResetComponent,
        canActivate: [authGuard]
      }
    ]
  },

  /** ROUTES WITHOUT NAVBAR (AUTH) - accessible only to guests */
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { 
        path: 'login', 
        component: LoginComponent,
        canActivate: [guestGuard]
      },
      { 
        path: 'register', 
        component: RegisterComponent,
        canActivate: [guestGuard]
      },
      { 
        path: 'forgot-password', 
        component: ForgotPasswordComponent,
        canActivate: [guestGuard]
      },
      { 
        path: 'reset-password', 
        component: ResetPasswordComponent
        // No guard - token-based access
      },
      { 
        path: 'activate-account', 
        component: ActivateAccountComponent
        // No guard - token-based access
      }
    ]
  }
];