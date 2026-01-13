import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { VehicleLocationDTO } from '../models/vehicle.model';

@Injectable({ providedIn: 'root' })
export class VehicleService {
  private apiUrl = 'http://localhost:8080/api/unregistered-users';

  constructor(private http: HttpClient) {}

  getActiveVehicles(): Observable<VehicleLocationDTO[]> {
    return this.http.get<VehicleLocationDTO[]>(`${this.apiUrl}/active-vehicles`);
  }
}
