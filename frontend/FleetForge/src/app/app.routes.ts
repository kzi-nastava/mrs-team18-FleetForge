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
import { CurrentRidePassengerComponent } from './passenger/current-ride-passenger/current-ride-passenger.component';
import { CurrentRideDriverComponent } from './driver/current-ride-driver/current-ride-driver.component';
import { DriverDashboardComponent } from './driver/driver-dashboard/driver-dashboard.component';

import { authGuard, guestGuard, roleGuard } from './auth/guard/auth-guard';
import { homeRedirectGuard } from './auth/guard/home-redirect-guard';
import { PassengerHistoryComponent } from './passenger/passenger-history/passenger-history.component';
import { PassengerFavoriteRoutesComponent } from './passenger/passenger-favorite-routes/passenger-favorite-routes.component';
import { PassengerDashboardComponent } from './passenger/dashboard/dashboard.component';
import { PassengerScheduledRidesComponent } from './passenger/passenger-scheduled-rides/passenger-scheduled-rides.component';
import { PassengerLiveChatComponent } from './passenger/live-chat/live-chat.component';
import { DriverLiveChatComponent } from './driver/live-chat/live-chat.component';
import { AdminLiveChatComponent } from './admin/live-chat/live-chat.component';
import { AdminHistoryComponent } from './admin/admin-history/admin-history.component';
import { AdminActiveRidesComponent } from './admin/admin-active-rides/admin-active-rides.component';
import { PriceManagementComponent } from './admin/price-management/price-management.component';
import { UsersListComponent } from './admin/users-list/users-list.component';
import { ReportsComponent } from './passenger/reports/reports.component';
import { DriverReportsComponent } from './driver/driver-reports/driver-reports.component';
import { AdminReportsComponent } from './admin/admin-reports/admin-reports.component';
import { AdminHomeComponent } from './admin/admin-home/admin-home.component';

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
      {
        path: 'passenger/current-ride',
        component: CurrentRidePassengerComponent,
        canActivate: [authGuard, roleGuard(['PASSENGER'])]
      },
      {
        path: 'passenger/ride-history',
        component: PassengerHistoryComponent,
        canActivate: [authGuard, roleGuard(['PASSENGER'])]
      },
      {
        path: 'passenger/favourite-rides',
        component: PassengerFavoriteRoutesComponent,
        canActivate: [authGuard, roleGuard(['PASSENGER'])]
      },
      {
        path: 'passenger/dashboard',
        component: PassengerDashboardComponent,
        canActivate: [authGuard, roleGuard(['PASSENGER'])]
      },
      {
        path: 'passenger/scheduled-rides',
        component: PassengerScheduledRidesComponent,
        canActivate: [authGuard, roleGuard(['PASSENGER'])]
      },
      {
        path: 'passenger/live-chat',
        component: PassengerLiveChatComponent,
        canActivate: [authGuard, roleGuard(['PASSENGER'])]
      },{
        path:'passenger/reports',
        component: ReportsComponent,
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
      {
        path: 'admin/live-chat',
        component: AdminLiveChatComponent,
        canActivate: [authGuard, roleGuard(['ADMIN'])]
      },
      {
        path: 'admin/admin-history',
        component: AdminHistoryComponent,
        canActivate: [authGuard, roleGuard(['ADMIN'])]
      },
      {
        path: 'admin/admin-home',
        component: AdminHomeComponent,
        canActivate: [authGuard, roleGuard(['ADMIN'])]
      },
      {
        path: 'admin/active-rides',
        component: AdminActiveRidesComponent,
        canActivate: [authGuard, roleGuard(['ADMIN'])]
      },
      {
        path: 'admin/price-management',
        component: PriceManagementComponent,
        canActivate: [authGuard, roleGuard(['ADMIN'])]
      },{
          path: 'admin/users-list',
          component: UsersListComponent,
          canActivate: [authGuard, roleGuard(['ADMIN'])]
      },{
          path: 'admin/reports',
          component:AdminReportsComponent,
          canActivate: [authGuard, roleGuard(['ADMIN'])]
      },

      // Driver routes - require authentication and DRIVER role
      {
        path: 'driver/dashboard',
        component: DriverDashboardComponent,
        canActivate: [authGuard, roleGuard(['DRIVER'])]
      },
      {
        path: 'profile-driver',
        component: DriverProfileComponent,
        canActivate: [authGuard, roleGuard(['DRIVER'])]
      },
      {
        path : 'driver/current-ride',
        component: CurrentRideDriverComponent,
        canActivate: [authGuard, roleGuard(['DRIVER'])]
      },
      {
        path: 'driver/ride-history',
        component: DriverHistoryComponent,
        canActivate: [authGuard, roleGuard(['DRIVER'])]
      },
      {
        path: 'driver/live-chat',
        component: DriverLiveChatComponent,
        canActivate: [authGuard, roleGuard(['DRIVER'])]
      },
      {
          path: 'driver/reports',
          component:DriverReportsComponent,
          canActivate: [authGuard, roleGuard(['DRIVER'])]
      },
      {
        path: 'set-password',
        component: PasswordSetComponent
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
