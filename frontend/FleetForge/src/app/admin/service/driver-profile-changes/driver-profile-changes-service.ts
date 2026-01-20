import { Injectable, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChangeAcceptanceDTO, ChangeAcceptanceResponseDTO, DriverProfileChangePendingRequestsDTO, VehicleProfileChangePendingRequestsDTO } from '../../../shared/dtos/driver-profile-requests.dtos';

@Injectable({
  providedIn: 'root',
})
export class DriverProfileChangesService{
  private apiUrl = 'http://localhost:8080/api/admin';
  constructor(private http: HttpClient) {}
 
  findAllPendingRequests(): Observable<DriverProfileChangePendingRequestsDTO[]>{
    return this.http.get<DriverProfileChangePendingRequestsDTO[]>(`${this.apiUrl}/profile-change-requests`);
  }

  findAllVehiclePendingRequests(): Observable<VehicleProfileChangePendingRequestsDTO[]>{
    return this.http.get<VehicleProfileChangePendingRequestsDTO[]>(`${this.apiUrl}/vehicle-change-requests`);
  }

    sendDriverChangeDecision(request:ChangeAcceptanceDTO,requestId:number): Observable<ChangeAcceptanceResponseDTO>{
    return this.http.put<ChangeAcceptanceResponseDTO>(`${this.apiUrl}/${requestId}/driver-info`,  request);

  }
    sendVehicleChangeDecision(request:ChangeAcceptanceDTO,requestId:number): Observable<ChangeAcceptanceResponseDTO>{
    return this.http.put<ChangeAcceptanceResponseDTO>(`${this.apiUrl}/${requestId}/vehicle-info`, request);

  }
}
