import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DriverCreateRequestDTO, DriverCreateResponseDTO } from '../../../shared/dtos/driver.dtos';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RegisterDriverService {
  private apiDriverUrl = 'http://localhost:8080/api/drivers';
  private apiUsersUrl = 'http://localhost:8080/api/users';
  constructor(private http: HttpClient) {}
  
  registerDriver(request:DriverCreateRequestDTO): Observable<DriverCreateResponseDTO> {
    return this.http.post<DriverCreateResponseDTO>(`${this.apiDriverUrl}`, request);
  }
  uploadProfilePicture(formData: FormData, id: number): Observable<any> {
    return this.http.post<any>(`${this.apiUsersUrl}/upload-profile-picture/${id}`, formData);
  }
}
