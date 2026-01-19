import { Component, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { LogoComponent } from '../../shared/logo/logo.component';
import { SidebarService } from '../../navigation/sidebar/sidebar.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    LogoComponent,
    CommonModule,
    FormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {

  email = '';
  password = '';
  error = '';
  submitted = false;

  ngOnInit() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }

  constructor(
    private sidebarService: SidebarService,
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef 
  ) {}

  login(form: any): void {
    this.submitted = true;

    if (form.invalid) {
      Object.keys(form.controls).forEach(key => {
        form.controls[key].markAsTouched();
      });
      return;
    }

    this.error = '';

    this.authService.login({
      email: this.email,
      password: this.password
    }).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);

        this.sidebarService.setAuthenticated(true);
        this.sidebarService.setUserRole(
          response.role.replace('ROLE_', '') as any
        );

        if(response.role === 'ROLE_PASSENGER') {
          this.router.navigate(['/passenger/passenger-home']);
          return;
        }
        this.router.navigate(['/']);
      },
      error: () => {
        this.error = 'Invalid email or password! Please try again.';
        this.submitted = false; 
        this.cdr.detectChanges();
      }
    });
  }

}