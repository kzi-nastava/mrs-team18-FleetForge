import { Component, ChangeDetectorRef } from '@angular/core';
import { LogoComponent } from '../../shared/logo/logo.component';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  imports: [CommonModule, LogoComponent, FormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})

export class ForgotPasswordComponent {

  submitted = false;
  email = '';
  serverError = '';

  showPopup = false;
  popupMessage = '';
  popupSuccess = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }

  sendResetLink(form: any) {
    this.submitted = true;

    if (form.invalid) {
      Object.keys(form.controls).forEach(key => {
        form.controls[key].markAsTouched();
      });
      return;
    }

    this.serverError = '';

    this.authService.requestPasswordReset({ email: this.email }).subscribe({
      next: (response) => {
        this.popupSuccess = true;
        this.popupMessage = 'Reset link has been sent to your email.';
        this.showPopup = true;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.popupSuccess = false;
        this.popupMessage = 'There has been an error sending the reset link.';
        this.showPopup = true;
        this.cdr.detectChanges();
      }
    });
  }

  closePopup() {
    this.showPopup = false;

    if (this.popupSuccess) {
      this.router.navigate(['/login']);
    }
  }
}