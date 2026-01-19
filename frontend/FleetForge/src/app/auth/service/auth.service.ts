import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { SidebarService } from '../../navigation/sidebar/sidebar.service';
import { Router } from '@angular/router';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  role: string;
  loggedInAt: string;
}

export interface PasswordResetRequest {
  email: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(
    private http: HttpClient,
    private sidebarService: SidebarService,
    private router: Router
  ) {
    this.checkAuthStatus();
  }

  private checkAuthStatus(): void {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    
    if (token && role) {
      this.sidebarService.setAuthenticated(true);
      this.sidebarService.setUserRole(role as any);
    }
  }

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, data).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);
        
        this.sidebarService.setAuthenticated(true);
        this.sidebarService.setUserRole(response.role as any);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    
    this.sidebarService.setAuthenticated(false);
    this.sidebarService.setUserRole(null);
    
    this.router.navigate(['/']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  requestPasswordReset(data: PasswordResetRequest): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/password-reset-requests`,
      data
    );
  }
  
  resetPassword(data: { token: string; newPassword: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/password-resets`, data);
  }

  register(data: FormData) {
    return this.http.post<void>(`${this.apiUrl}/register`, data);
  }

  checkEmailAvailability(email: string) {
    return this.http.get<{ available: boolean }>(
      `${this.apiUrl}/email-availability`,
      { params: { email } }
    );
  } 

  activateAccount(token: string) {
    return this.http.get(
      this.apiUrl+`/activations?token=${token}`,
      { observe: 'response' }
    );
  }
}