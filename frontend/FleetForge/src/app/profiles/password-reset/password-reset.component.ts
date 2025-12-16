import { CommonModule } from '@angular/common';
import { Component, ViewEncapsulation } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router, RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { routes } from '../../app.routes';

@Component({
  selector: 'app-password-reset',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule],
  templateUrl: './password-reset.component.html',
  styleUrl: './password-reset.component.css',
  encapsulation: ViewEncapsulation.None
})
export class PasswordResetComponent {
  constructor(protected router: Router, protected location: Location) {}

  editUsersPassword=new FormGroup({
    newPassword: new FormControl('', Validators.required),
    repeatPassword: new FormControl('', Validators.required)
  });

  resetPassword(): void {
    if (this.editUsersPassword.invalid) return;

    const newPassword = this.editUsersPassword.value.newPassword ?? '';
    const repeatPassword = this.editUsersPassword.value.repeatPassword ?? '';
    if (newPassword !== repeatPassword) {
      alert('Passwords do not match!');
      return;
    }
    alert('Password successfully changed!');
    this.location.back();
  }

}
