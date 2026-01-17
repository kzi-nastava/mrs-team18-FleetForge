import { Component, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
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


  serverError = '';

  showPopup = false;
  popupMessage = '';
  popupSuccess = false;

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

    if (form.invalid || this.password !== this.confirmPassword) {
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
        this.popupSuccess = true;
        this.popupMessage = 'Registration request successfully submitted. The verification email has been sent.';
        this.showPopup = true;
        this.cdr.detectChanges();
      },
      error: () => {
        this.popupSuccess = false;
        this.popupMessage = 'Registration failed. Please try again.';
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

  passwordsDoNotMatch() {
    return this.password !== this.confirmPassword;
  }

  onProfilePictureSelected(event: Event) {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files.length > 0) {
    this.selectedProfilePicture = input.files[0];
  }
}


}
