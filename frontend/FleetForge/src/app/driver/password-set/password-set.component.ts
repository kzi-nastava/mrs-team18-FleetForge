import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-password-set',
  imports: [CommonModule,ReactiveFormsModule],
  templateUrl: './password-set.component.html',
  styleUrl: './password-set.component.css',
})
export class PasswordSetComponent {
resetPassword(): void {

}
editUsersPassword =new FormGroup({
    newPassword: new FormControl('', Validators.required),
    repeatPassword: new FormControl('', Validators.required)
  });
}
