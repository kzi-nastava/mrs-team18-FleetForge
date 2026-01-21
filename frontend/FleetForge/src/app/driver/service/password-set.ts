import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PasswordSetDTO, PasswordSetResponseDTO } from '../../shared/dtos/driver.dtos';
import { Observable } from 'rxjs';
import { ValidateTokenResponseDTO } from '../../shared/dtos/token.dtos';

@Injectable({
  providedIn: 'root',
})
export class PasswordSet {

  constructor(private http: HttpClient) { }
  
  setPassword(token: string, newPassword: string): Observable<PasswordSetResponseDTO> {
    const request: PasswordSetDTO = { token, password: newPassword };
    return this.http.post<PasswordSetResponseDTO>(`http://localhost:8080/api/drivers/set-password`, request);
  }
  
  validateToken(token: string): Observable<ValidateTokenResponseDTO> {
    return this.http.get<ValidateTokenResponseDTO>(`http://localhost:8080/api/auth/validate-token?token=${token}`);
  }
}
