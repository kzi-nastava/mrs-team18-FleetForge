import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RideEstimateRequestDTO, RideEstimateResponseDTO } from '../dtos/price-configuration.dtos';

@Injectable({
  providedIn: 'root'
})
export class RideEstimateService {
  private apiUrl = 'http://localhost:8080/api/ride-estimates';

  constructor(private http: HttpClient) {}

  estimateRide(request: RideEstimateRequestDTO): Observable<RideEstimateResponseDTO> {
    return this.http.post<RideEstimateResponseDTO>(this.apiUrl, request);
  }
}
