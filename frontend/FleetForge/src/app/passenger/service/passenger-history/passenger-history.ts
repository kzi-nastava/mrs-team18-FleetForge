import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface PassengerRideResponse {
  content: PassengerRideDTO[];
}

type RideStatus = 'COMPLETED' | 'CANCELLED' | 'IN_PROGRESS';

export interface PassengerRideDTO {
  rideId: number;
  startAddress: string;
  endAddress: string;
  startTime: string;
  endTime: string | null;
  status: RideStatus;
  startLocation: { latitude: number; longitude: number };
  endLocation: { latitude: number; longitude: number };
  vehicleRating: number | null;
  driverRating: number | null;
  averageReview: number | null;
}

@Injectable({ providedIn: 'root' })
export class PassengerHistory {
  private apiUrl = 'http://localhost:8080/api/passenger';

  constructor(private http: HttpClient) {}

  getPassengerRides(): Observable<PassengerRideResponse> {
    return this.http.get<PassengerRideResponse>(`${this.apiUrl}/rides`);
  }

  addFavoriteRoute(name: string, rideId: number) {
    return this.http.post<void>(`${this.apiUrl}/favorites/${name}/${rideId}`, {});
  }

  deleteFavoriteRoute(routeId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/favorites/${routeId}`);
  }
}
