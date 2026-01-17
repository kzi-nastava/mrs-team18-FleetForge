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
    return this.http.post<PasswordSetResponseDTO>(`http://localhost:8080/api/drivers/set-password`, request, {
       headers: {
         Authorization: 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJGbGVldEZvcmdlQXBwIiwic3ViIjoiYWRtaW5AdGVzdC5jb20iLCJpYXQiOjE3Njg2MTM3NDMsImV4cCI6MTc2ODYxNTU0Mywicm9sZXMiOiJST0xFX0FETUlOIn0.8IFeb7Pf6-KHRAZkxC5-PoQ33ySb9Q2WKPRr4fqbzhPUxnizRza4M05nnQiiyGqrnpDGGJz05EYkGFk6kNuMRw'
       }
    });
  }
  
  validateToken(token: string): Observable<ValidateTokenResponseDTO> {
    return this.http.get<ValidateTokenResponseDTO>(`http://localhost:8080/api/auth/validate-token?token=${token}`, {
       headers: {
         Authorization: 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJGbGVldEZvcmdlQXBwIiwic3ViIjoiYWRtaW5AdGVzdC5jb20iLCJpYXQiOjE3Njg2MTM3NDMsImV4cCI6MTc2ODYxNTU0Mywicm9sZXMiOiJST0xFX0FETUlOIn0.8IFeb7Pf6-KHRAZkxC5-PoQ33ySb9Q2WKPRr4fqbzhPUxnizRza4M05nnQiiyGqrnpDGGJz05EYkGFk6kNuMRw'
       }
    });
  }
}
