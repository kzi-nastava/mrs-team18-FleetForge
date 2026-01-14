import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DriverChangeRequestDTO, DriverChangeResponseDTO, DriverInformationDTO, UserInformationDTO } from '../../shared/dtos/users.dtos';
import { Vehicle } from '../model/vehicle.model';
import { VehicleChangeRequestDTO, VehicleChangeResponseDTO } from '../../shared/dtos/vehicle.dtos';

@Injectable({
  providedIn: 'root',
})
export class DriverService {
  private apiUrl = 'http://localhost:8080/api/drivers';

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
  
}
