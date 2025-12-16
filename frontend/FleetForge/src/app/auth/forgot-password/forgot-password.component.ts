import { Component } from '@angular/core';
import { LogoComponent } from '../../shared/logo/logo.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  imports: [LogoComponent],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent {
constructor(private router: Router) {}

  navigateToResetPassword() {
    this.router.navigate(['/reset-password']);
  }
}
