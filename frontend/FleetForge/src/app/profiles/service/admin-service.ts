import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserInformationDTO } from '../../shared/dtos/users.dtos';
import { Admin } from '../../shared/models/user.model';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private apiUrl='http://localhost:8080/api/admin';
  constructor(private http:HttpClient) {}
  
  getCurrentAdmin():Observable<UserInformationDTO> {
    return this.http.get<UserInformationDTO>(`${this.apiUrl}`);
  }

  changeAdmin(user:UserInformationDTO):Observable<UserInformationDTO> {
    return this.http.put<UserInformationDTO>(`${this.apiUrl}`, user);
  }
  
}
