import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LogoComponent } from '../../shared/logo/logo.component';
import { SidebarService } from '../../navigation/sidebar/sidebar.service';
import { UserRole } from '../../navigation/sidebar/sidebar-config';

@Component({
  selector: 'app-login',
  imports: [LogoComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  constructor(
    private sidebarService: SidebarService,
    private router: Router
  ) {}

  // Mock login - simulates successful login for testing
  mockLogin(role: UserRole): void {
    // Set authentication state
    this.sidebarService.setAuthenticated(true);
    // Set user role
    this.sidebarService.setUserRole(role);
    // Navigate to home page
    this.router.navigate(['/']);
  }

  // Quick test methods for each role
  loginAsUser(): void {
    this.mockLogin('USER');
  }

  loginAsDriver(): void {
    this.mockLogin('DRIVER');
  }

  loginAsAdmin(): void {
    this.mockLogin('ADMIN');
  }
}
