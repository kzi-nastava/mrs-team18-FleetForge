import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

  constructor(private http: HttpClient) {}

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, data);
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
      `http://localhost:8080/api/auth/activations?token=${token}`,
      { observe: 'response' }
    );
  }

}
