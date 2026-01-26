import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';

export interface UpcomingRideDTO {
  rideId: number;
  passengerName: string;
  passengerImage: string;
  startAddress: string;
  endAddress: string;
  startTime: string;
  cost: number;
}

export interface DailyStatsDTO {
  totalEarnings: number;
  hoursWorked: number;
  ridesCompleted: number;
}

export interface DashboardData {
  upcomingRides: UpcomingRideDTO[];
  dailyStats: DailyStatsDTO;
}

@Injectable({ providedIn: 'root' })
export class DriverDashboardService {
  private apiUrl = 'http://localhost:8080/api/drivers';

  constructor(private http: HttpClient) {}

  getUpcomingRides(): Observable<UpcomingRideDTO[]> {
    return this.http.get<UpcomingRideDTO[]>(`${this.apiUrl}/upcoming-rides`);
  }

  getDailyStats(): Observable<DailyStatsDTO> {
    return this.http.get<DailyStatsDTO>(`${this.apiUrl}/daily-stats`);
  }

  getDashboardData(): Observable<DashboardData> {
    return forkJoin({
      upcomingRides: this.getUpcomingRides(),
      dailyStats: this.getDailyStats()
    });
  }
}
