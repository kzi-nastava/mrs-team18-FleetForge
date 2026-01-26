import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RideService {
  private apiUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) { }

  cancelRide(rideId: number, reason?: string) {
    return this.http.post(
      `${this.apiUrl}/${rideId}/cancellations`,
      reason ? { reason } : {}
    );
  }
  
}
