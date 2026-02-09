import { Injectable, OnInit, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChangeAcceptanceDTO, ChangeAcceptanceResponseDTO, DriverProfileChangePendingRequestsDTO, VehicleProfileChangePendingRequestsDTO } from '../../../shared/dtos/driver-profile-requests.dtos';

export interface AdminRide {
  rideId: number;
  startTime: string;
  endTime: string;
  startAddress: string;
  destinationAddress: string;
  status: string;
  panicActivated: boolean;
}

export interface AdminRideDetailsDto {
  id: number;
  startAddress: string;
  endAddress: string;

  startLocation: { latitude: number; longitude: number };
  endLocation: { latitude: number; longitude: number };
  wayPoints: { latitude: number; longitude: number }[];

  startTime: string;
  endTime: string;

  totalDistance: number;
  estimatedDuration: number;
  totalCost: number;

  status: string;
  vehicleType: string;

  cancelledBy?: 'DRIVER' | 'PASSENGER' | 'SYSTEM' | null;
  cancellationReason?: string | null;

  driver: {
    id: number;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    profileImage: string;
  };

  mainPassenger: {
    id: number;
    firstName: string;
    lastName: string;
  };

  linkedPassengers: {
    id: number;
    firstName: string;
    lastName: string;
  }[];

  ratings?: {
    driverRating?: number;
    vehicleRating?: number;
  };
}


export interface AdminRideResponse {
  content: AdminRide[];
  totalPages: number;
}

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

  searchUsersByPrefix(prefix: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/search-users?prefix=${prefix}`);
  }

  getRides(
    email?: string,
    startDate?: string,
    endDate?: string,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'startTime',
    direction: 'asc' | 'desc' = 'desc'
  ): Observable<AdminRideResponse> {
    const params: any = {
      page,
      size,
      sortBy,
      direction,
    };

    if (email) params.email = email;
    if (startDate) params.from = startDate;
    if (endDate) params.to = endDate;

    return this.http.get<AdminRideResponse>(`${this.apiUrl}/rides`, { params });
  }

  getRideDetails(rideId: number) {
    return this.http.get<AdminRideDetailsDto>(
      `${this.apiUrl}/rides/${rideId}`
    );
  }

}
