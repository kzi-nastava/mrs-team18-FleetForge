import { Component, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../service/auth.service';
import { PopupDialogComponent   } from '../../shared/popup-dialog/popup-dialog.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, PopupDialogComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {

  submitted = false;

  firstName = '';
  lastName = '';
  email = '';
  phone = '';
  password = '';
  confirmPassword = '';
  address = '';
  selectedProfilePicture: File | null = null;
  
  emailAvailable: boolean | null = null;
  checkingEmail = false;

  serverError = '';

  showPopup = false;
  popupMessage = '';
  popupTitle = '';        
  popupSuccess = false;
  popupButtonText = '';   

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }

  register(form: any) {
    this.submitted = true;

    if (form.invalid || this.password !== this.confirmPassword || this.emailAvailable === false) {
      Object.keys(form.controls).forEach(key => {
        form.controls[key].markAsTouched();
      });
      return;
    }

    const formData = new FormData();
    formData.append('email', this.email);
    formData.append('password', this.password);
    formData.append('firstName', this.firstName);
    formData.append('lastName', this.lastName);
    formData.append('address', this.address);
    formData.append('phoneNumber', this.phone);

    if (this.selectedProfilePicture) {
      formData.append('profilePicture', this.selectedProfilePicture);
    }

    this.authService.register(formData).subscribe({
      next: () => {
        this.showSuccess('Registration request successfully submitted. The verification email has been sent.');
        this.cdr.detectChanges();
      },
      error: () => {
        this.showError('Registration failed. Please try again.');
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

  passwordsDoNotMatch() {
    return this.password !== this.confirmPassword;
  }

  onProfilePictureSelected(event: Event) {
      const input = event.target as HTMLInputElement;
      if (input.files && input.files.length > 0) {
        this.selectedProfilePicture = input.files[0];
      }
    }

    checkEmailAvailability() {
    if (!this.email) return;

    this.checkingEmail = true;

    this.authService.checkEmailAvailability(this.email).subscribe({
      next: (res) => {
        this.emailAvailable = res.available;
        this.checkingEmail = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.emailAvailable = null;
        this.checkingEmail = false;
      }
    });
  }
}
