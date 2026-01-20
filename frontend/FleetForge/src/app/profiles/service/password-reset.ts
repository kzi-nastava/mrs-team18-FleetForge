import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PasswordResetDTO } from '../../shared/dtos/password.dtos';

@Injectable({
  providedIn: 'root',
})
export class PasswordReset {
  private driverApiUrl = 'http://localhost:8080/api/drivers';
  private adminApiUrl = 'http://localhost:8080/api/admin';
  private passengerApiUrl = 'http://localhost:8080/api/passenger';

  constructor(private http:HttpClient) { }

  resetPassword(req:PasswordResetDTO):Observable<any>{
    const role=localStorage.getItem('role');
    if(role==='DRIVER'){
      return this.http.put<string>(`${this.driverApiUrl}/password`, req);
    } else if(role==='ADMIN'){
      return this.http.put<string>(`${this.adminApiUrl}/password`, req);
    } else if(role==='PASSENGER'){
      return this.http.put<string>(`${this.passengerApiUrl}/password`, req);
    }
    throw new Error('Invalid role');
  }
  
}
