import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RideTrackingDTO } from '../../../shared/dtos/ride-tracking.dtos';

@Injectable({
  providedIn: 'root',
})
export class PassengerCurrentRide {
  private apiUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {}

  /**
   * Get active ride tracking for the currently logged-in passenger
   * GET /api/rides/active-tracking
   * Returns 404 if no active ride exists
   */
  getActiveTracking(): Observable<RideTrackingDTO> {
    return this.http.get<RideTrackingDTO>(`${this.apiUrl}/active-tracking`);
  }
}
