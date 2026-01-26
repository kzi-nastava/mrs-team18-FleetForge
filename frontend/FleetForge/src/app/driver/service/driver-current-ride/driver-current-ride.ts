import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DriverLocationUpdateRequestDTO, DriverLocationUpdateResponseDTO } from '../../../shared/dtos/driver-location-update.dto';
import { RideTrackingDTO } from '../../../shared/dtos/ride-tracking.dtos';
import { RideStartResponseDTO } from '../../../shared/dtos/ride.dtos';

@Injectable({
  providedIn: 'root',
})
export class DriverCurrentRide {
  private apiUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {}

  updateLocation(request: DriverLocationUpdateRequestDTO): Observable<DriverLocationUpdateResponseDTO> {
    return this.http.post<DriverLocationUpdateResponseDTO>(
      `${this.apiUrl}/driver-location-update`,
      request
    );
  }

  getActiveTracking(): Observable<RideTrackingDTO> {
    return this.http.get<RideTrackingDTO>(`${this.apiUrl}/active-tracking`);
  }

  startRide(rideId: string): Observable<RideStartResponseDTO> {
    return this.http.put<RideStartResponseDTO>(`${this.apiUrl}/${rideId}/start`, {});
  }
}
