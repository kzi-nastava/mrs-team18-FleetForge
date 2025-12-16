import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ResetPasswordComponent } from './auth/reset-password/reset-password.component';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password.component';
import { UserProfileDriverComponent } from './profiles/user-profile-driver/user-profile-driver.component';
import { UserProfileComponent } from './profiles/user-profile/user-profile.component';
import { PasswordResetComponent } from './profiles/password-reset/password-reset.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
   {path:'profile',component:UserProfileComponent},
  {path:'profile-driver',component:UserProfileDriverComponent},
  {path:'profile-password-reset',component:PasswordResetComponent}
];
