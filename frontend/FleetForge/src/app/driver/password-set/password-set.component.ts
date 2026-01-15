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
<<<<<<< Updated upstream
editUsersPassword=new FormGroup({
=======
editUsersPassword =new FormGroup({
>>>>>>> Stashed changes
    newPassword: new FormControl('', Validators.required),
    repeatPassword: new FormControl('', Validators.required)
  });
}
