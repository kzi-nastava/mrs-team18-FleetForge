import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DriverChangeRequestDTO, DriverChangeResponseDTO, DriverInformationDTO, GetIsBlockedUserDTO, UserInformationDTO } from '../../shared/dtos/users.dtos';
import { Vehicle } from '../model/vehicle.model';
import { VehicleChangeRequestDTO, VehicleChangeResponseDTO } from '../../shared/dtos/vehicle.dtos';
import { DriverActivityResponseDTO } from '../../shared/dtos/driver.dtos';

@Injectable({
  providedIn: 'root',
})
export class DriverService {
  private apiUrl = 'http://localhost:8080/api/drivers';
  private apiUsersUrl='http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getCurrentDriver():Observable<DriverInformationDTO> {
    return this.http.get<DriverInformationDTO>(`${this.apiUrl}`);
  }

  createChangeRequest(request:DriverChangeRequestDTO):Observable<DriverChangeResponseDTO> {
    return this.http.post<DriverChangeResponseDTO>(`${this.apiUrl}/update-request`, request);
  }

  createVehicleChangeRequest(request:VehicleChangeRequestDTO):Observable<VehicleChangeResponseDTO> {
    return this.http.post<VehicleChangeResponseDTO>(`${this.apiUrl}/update-request-vehicle`, request);
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
  getDriverActivty():Observable<DriverActivityResponseDTO> {
    return this.http.get<DriverActivityResponseDTO>(`${this.apiUrl}/active-hours`);
  }

  getIsBlocked():Observable<GetIsBlockedUserDTO>{
      return this.http.get<GetIsBlockedUserDTO>(this.apiUsersUrl+"/blocked");
    }
}
