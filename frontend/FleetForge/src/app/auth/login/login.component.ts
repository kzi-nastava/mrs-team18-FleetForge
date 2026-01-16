import { Component } from '@angular/core';
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

  constructor(
    private sidebarService: SidebarService,
    private router: Router,
    private authService: AuthService
  ) {}

  login(): void {
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

        this.router.navigate(['/']);
      },
      error: () => {
        this.error = 'Invalid email or password';
      }
    });
  }
}
