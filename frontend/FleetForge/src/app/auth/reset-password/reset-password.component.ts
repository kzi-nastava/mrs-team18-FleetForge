import { Component, ChangeDetectorRef } from '@angular/core';
import { LogoComponent } from '../../shared/logo/logo.component';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { PopupDialogComponent   } from '../../shared/popup-dialog/popup-dialog.component';


@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [LogoComponent, CommonModule, FormsModule, PopupDialogComponent],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css',
})
export class ResetPasswordComponent {
  token: string | null = null;

  newPassword = '';
  confirmPassword = '';

  submitted = false;

  showPopup = false;
  popupMessage = '';
  popupSuccess = false;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');

    this.token = this.route.snapshot.queryParamMap.get('token');
  }

  passwordsDoNotMatch(): boolean {
    return this.newPassword !== this.confirmPassword;
  }

  resetPassword(form: NgForm) {
    this.submitted = true;

    if (!this.token) {
      this.popupSuccess = false;
      this.popupMessage = 'Invalid or missing reset token.';
      this.showPopup = true;
      this.cdr.detectChanges();
      return;
    }

    if (form.invalid || this.passwordsDoNotMatch()) {
      return;
    }

    this.authService
      .resetPassword({ token: this.token, newPassword: this.newPassword })
      .subscribe({
        next: () => {
          this.popupSuccess = true;
          this.popupMessage = 'Password successfully changed!';
          this.showPopup = true;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.popupSuccess = false;
          this.popupMessage =
            err?.error?.message || 'Reset password failed. Token may be expired.';
          this.showPopup = true;
          this.cdr.detectChanges();
        },
      });
  }

  closePopup() {
    this.showPopup = false;

    if (this.popupSuccess) {
      this.router.navigate(['/login']);
    }
  }
}
