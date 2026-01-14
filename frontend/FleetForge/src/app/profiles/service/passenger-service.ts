import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';;
import { UserInformationDTO } from '../../shared/dtos/users.dtos';

@Injectable({
  providedIn: 'root',
})
export class PassengerService {
  private apiUrl='http://localhost:8080/api/passenger';

  constructor(private http:HttpClient) {}

  getCurrentPassenger():Observable<UserInformationDTO> {
    return this.http.get<UserInformationDTO>(`${this.apiUrl}`);
  }
  // , {
  //      headers: {
  //        Authorization: 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJGbGVldEZvcmdlQXBwIiwic3ViIjoiZHJpdmVyMUB0ZXN0LmNvbSIsImlhdCI6MTc2ODQxNzQ0OSwiZXhwIjoxNzY4NDE5MjQ5LCJyb2xlcyI6IlJPTEVfRFJJVkVSIn0.kQwWlq3vaJDZKGkv8MCBzGGXE2h6rLQRJfamvbkoN7y0SNcBfrUAIcH-N6Lx7-pZDD6Yvhisr_DiHKSWf6f2Pg'
  //      }
  //   });
  changePassenger(user:UserInformationDTO):Observable<UserInformationDTO> {
    return this.http.put<UserInformationDTO>(`${this.apiUrl}`, user);
  }
}
