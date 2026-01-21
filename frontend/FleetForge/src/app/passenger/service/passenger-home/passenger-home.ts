import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RideCreateRequestDTO, RideCreateResponseDTO } from '../../../shared/dtos/ride.dtos';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PassengerHome {
  private apiUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {
  }

  createRide(req:RideCreateRequestDTO):Observable<RideCreateResponseDTO>{
    return this.http.post<RideCreateResponseDTO>(this.apiUrl+"/create", req);
  }
}
