import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';;
import { UserInformationChangeDTO, UserInformationDTO } from '../../shared/dtos/users.dtos';

@Injectable({
  providedIn: 'root',
})
export class PassengerService {
  private apiUrl='http://localhost:8080/api/passenger';
  private apiUsersUrl='http://localhost:8080/api/users';

  constructor(private http:HttpClient) {}

  getCurrentPassenger():Observable<UserInformationDTO> {
    return this.http.get<UserInformationDTO>(`${this.apiUrl}`);
  }
  changePassenger(user:UserInformationChangeDTO):Observable<UserInformationDTO> {
    return this.http.put<UserInformationDTO>(`${this.apiUrl}`, user);
  }
  changePassword(newPassword:string):Observable<void> {
    const passwordChangeDTO = {
      password: newPassword
    };
    return this.http.put<void>(`${this.apiUrl}/password`, passwordChangeDTO);
  }
   uploadProfilePicture(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.apiUsersUrl}/upload-profile-picture`, formData);
  }
}
