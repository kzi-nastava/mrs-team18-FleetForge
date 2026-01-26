import { Component, ChangeDetectorRef } from '@angular/core';
import { LogoComponent } from '../../shared/logo/logo.component';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NotificationPopupComponent   } from '../../shared/popups/popup-dialog/notification-popup.component';

@Component({
  selector: 'app-forgot-password',
  imports: [CommonModule, LogoComponent, FormsModule, NotificationPopupComponent],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})

export class ForgotPasswordComponent {

  submitted = false;
  email = '';
  serverError = '';

  showPopup = false;
  popupTitle = '';
  popupMessage = '';
  popupSuccess = true;
  popupButtonText = 'OK';

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
        this.showSuccess('Reset link has been sent to your email.');
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.showError('There has been an error sending the reset link.');
        this.cdr.detectChanges();
      }
    });
  }

  showSuccess(message: string) {
    this.popupTitle = 'Success';
    this.popupMessage = message;
    this.popupSuccess = true;
    this.popupButtonText = 'Done';
    this.showPopup = true;
  }

  showError(message: string) {
    this.popupTitle = 'Error';
    this.popupMessage = message;
    this.popupSuccess = false;
    this.popupButtonText = 'Close';
    this.showPopup = true;
  }

  closePopup() {
    this.showPopup = false;

    if (this.popupSuccess) {
      this.router.navigate(['/login']);
    }
  }
}