import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { PasswordSet } from '../service/password-set';
import { ActivatedRoute, ActivatedRouteSnapshot, Router } from '@angular/router';

@Component({
  selector: 'app-password-set',
  imports: [CommonModule,ReactiveFormsModule],
  templateUrl: './password-set.component.html',
  styleUrl: './password-set.component.css',
})
export class PasswordSetComponent {
  ngOnInit(): void {
    this.passwordSetService.validateToken(this.token).subscribe((response) => {
      if (!response.success) {
        alert('Invalid or expired token.');
        this.router.navigate(['']);
      }
    });
  }
  private token: string = '';
  constructor(private passwordSetService: PasswordSet, private route: ActivatedRoute, private router: Router) {
    this.token =this.route.snapshot.queryParamMap.get('token') || '';
  }
resetPassword(): void {
    if (this.editUsersPassword.invalid) {
      this.editUsersPassword.markAllAsTouched();
      return;
    }
    const newPassword = this.editUsersPassword.get('newPassword')?.value!;
    this.passwordSetService.setPassword(this.token, newPassword).subscribe((answer) => {
      if(answer.success){
        alert('Password successfully changed!');
        this.router.navigate(['']);
      } else {
        alert('Error changing password');
      }
    });
}


  passwordsMatchValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.get('newPassword')?.value;
      const repeatPassword = control.get('repeatPassword')?.value;
      
      if (password && repeatPassword && password !== repeatPassword) {
        return { passwordsMismatch: true };
      }
      
      return null;
    };
  }

  editUsersPassword = new FormGroup({
    newPassword: new FormControl('', [
      Validators.required,
      Validators.minLength(8) 
    ]),
    repeatPassword: new FormControl('', Validators.required)
  }, { validators: this.passwordsMatchValidator() }); 
}
