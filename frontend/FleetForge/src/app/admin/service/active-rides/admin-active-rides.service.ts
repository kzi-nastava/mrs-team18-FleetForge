import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ActiveRideDTO, ActiveRideDetailsDTO } from '../../../shared/dtos/active-ride.dtos';

@Injectable({
  providedIn: 'root'
})
export class AdminActiveRidesService {
  private apiUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {}

  /**
   * Fetches basic information for all currently active (IN_PROGRESS) rides.
   * Called once when the admin opens the active rides page.
   */
  getAllActiveRides(): Observable<ActiveRideDTO[]> {
    return this.http.get<ActiveRideDTO[]>(`${this.apiUrl}/active`);
  }

  /**
   * Fetches detailed live data for a specific active ride.
   * Should be polled every 2 seconds when monitoring a ride.
   */
  getActiveRideDetails(rideId: number): Observable<ActiveRideDetailsDTO> {
    return this.http.get<ActiveRideDetailsDTO>(`${this.apiUrl}/active/${rideId}`);
  }
}
