import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PriceConfigurationDTO, UpdatePriceConfigurationDTO } from '../dtos/price-configuration.dtos';
import { VehicleType } from '../models/vehicle.model';

@Injectable({
  providedIn: 'root'
})
export class PriceConfigurationService {
  private apiUrl = 'http://localhost:8080/api/price-configurations';

  constructor(private http: HttpClient) {}

  getAllPriceConfigurations(): Observable<PriceConfigurationDTO[]> {
    return this.http.get<PriceConfigurationDTO[]>(this.apiUrl);
  }

  getPriceConfigurationByVehicleType(vehicleType: VehicleType): Observable<PriceConfigurationDTO> {
    return this.http.get<PriceConfigurationDTO>(`${this.apiUrl}/${vehicleType}`);
  }

  updatePriceConfiguration(
    vehicleType: VehicleType,
    dto: UpdatePriceConfigurationDTO
  ): Observable<PriceConfigurationDTO> {
    return this.http.put<PriceConfigurationDTO>(`${this.apiUrl}/${vehicleType}`, dto);
  }
}
