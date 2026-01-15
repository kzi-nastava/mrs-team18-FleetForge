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
<<<<<<< Updated upstream
=======
import { PasswordSetComponent } from './driver/password-set/password-set.component';
import { RegisterDriverComponent } from './admin/register-driver/register-driver.component';
>>>>>>> Stashed changes

export const routes: Routes = [
  /** ROUTES WITH NAVBAR */
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'profile-passenger', component: PassengerProfileComponent },
      {path: 'profile-admin', component: AdminProfileComponent },
      { path: 'profile-driver', component: DriverProfileComponent },
      { path: 'profile-password-reset', component: PasswordResetComponent },
      { path: 'driver/ride-history', component: DriverHistoryComponent },
<<<<<<< Updated upstream
      { path: 'admin/driver-changes', component: DriverProfileChangesComponent }
=======
      { path: 'admin/driver-changes', component: DriverProfileChangesComponent },
      {path: 'admin/register-new-driver', component: RegisterDriverComponent},
      {path : 'password-set', component: PasswordSetComponent}
>>>>>>> Stashed changes
    ]
  },

  /** ROUTES WITHOUT NAVBAR (AUTH) */
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'forgot-password', component: ForgotPasswordComponent },
      { path: 'reset-password', component: ResetPasswordComponent }
    ]
  }
];
