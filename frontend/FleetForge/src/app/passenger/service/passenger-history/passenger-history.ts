import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { InconsistencyReportDto } from '../../../admin/service/driver-profile-changes/driver-profile-changes-service';

export interface PassengerRideResponse {
  content: PassengerRideHistoryDto[];
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

type RideStatus = 'COMPLETED' | 'CANCELLED' | 'IN_PROGRESS';

export interface PassengerRideHistoryDto {
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

export interface PassengerRideDetailsDto {
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
  petFriendly: boolean;
  babySeat: boolean;
  driver: {
    firstName: string;
    lastName: string;
    phoneNumber: string;
  };
  hasInconsistencies: boolean;
  inconsistencies: InconsistencyReportDto[];
}

@Injectable({ providedIn: 'root' })
  export class PassengerHistory {
    private apiUrl = 'http://localhost:8080/api/passenger';

    constructor(private http: HttpClient) {}

    getPassengerRides(
      page: number,
      size: number,
      sortBy: string,
      direction: 'asc' | 'desc',
      from?: string,
      to?: string
    ) {
      const params: any = { page, size, sortBy, direction };
      if (from) params.from = from;
      if (to) params.to = to;

      return this.http.get<PageResponse<PassengerRideHistoryDto>>(`${this.apiUrl}/rides`, { params });
    }


  getRideDetails(rideId: number) {
    return this.http.get<PassengerRideDetailsDto>(
      `${this.apiUrl}/rides/${rideId}`
    );
  }


  addFavoriteRoute(name: string, rideId: number) {
    return this.http.post<void>(`${this.apiUrl}/favorites/${name}/${rideId}`, {});
  }

  deleteFavoriteRoute(routeId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/favorites/${routeId}`);
  }
}
