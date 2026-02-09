import { HttpClient, HttpParams } from '@angular/common/http';
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


export interface ScheduledRideResponse {
  content: ScheduledRideDto[];
  totalPages: number;
  totalElements: number;
  number: number;
}

export interface ScheduledRideDto {
  id: number;
  pickup: string;
  dropoff: string;
  scheduledTime: string;
  estimatedCost: number;
  status: 'CANCELLED' | 'ACCEPTED';
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

  getScheduledRides(page: number, size: number): Observable<ScheduledRideResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ScheduledRideResponse>(`${this.apiUrl}/scheduled`, { params });
  }

  get isRideActive(): boolean {
    return this.activeRideSubject.value !== null;
  }
}
