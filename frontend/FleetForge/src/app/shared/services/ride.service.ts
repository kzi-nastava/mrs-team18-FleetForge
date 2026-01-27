import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { RideTrackingDTO } from '../dtos/ride-tracking.dtos';

export interface CancelRideResponse {
  success: boolean;
  message: string;
}

export interface PanicResponse {
  success: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class RideService {
  private apiUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {}

  cancelRide(rideId: number, reason?: string) {
    return this.http.post<CancelRideResponse>(
      `${this.apiUrl}/${rideId}/cancellations`,
      reason ? { reason } : {}
    );
  }

  triggerPanic(rideId: number) {
    return this.http.post<PanicResponse>(
      `${this.apiUrl}/${rideId}/panic`,
      {}
    );
  }
  private activeRideSubject = new BehaviorSubject<RideTrackingDTO | null>(null);
  activeRide$ = this.activeRideSubject.asObservable();

  setActiveRide(ride: RideTrackingDTO | null) {
    this.activeRideSubject.next(ride);
  }

  get isRideActive(): boolean {
    return this.activeRideSubject.value !== null;
  }
}
