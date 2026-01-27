import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CompletedRideDTO } from '../../shared/dtos/completed-ride.dto';

@Injectable({
  providedIn: 'root',
})
export class DriverHistoryService {
  private apiUrl = 'http://localhost:8080/api/drivers';

  constructor(private http: HttpClient) {}

  getDriverHistory(): Observable<CompletedRideDTO[]> {
    return this.http.get<CompletedRideDTO[]>(`${this.apiUrl}/ride-history`);
  }
}