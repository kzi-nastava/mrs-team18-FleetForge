import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface CancelRideResponse {
  success: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class RideService {
  private apiUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {}

  cancelRide(
    rideId: number,
    reason?: string
  ): Observable<CancelRideResponse> {
    return this.http.post<CancelRideResponse>(
      `${this.apiUrl}/${rideId}/cancellations`,
      reason ? { reason } : {}
    );
  }
}
