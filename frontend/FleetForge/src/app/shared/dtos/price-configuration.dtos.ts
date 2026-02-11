import { VehicleType } from "../models/vehicle.model";

export interface PriceConfigurationDTO {
  id: number;
  vehicleType: VehicleType;
  basePrice: number;
  pricePerKm: number;
}

export interface UpdatePriceConfigurationDTO {
  basePrice: number;
  pricePerKm: number;
}

export interface RideEstimateRequestDTO {
  distanceKm: number;
  vehicleType: VehicleType;
}

export interface RideEstimateResponseDTO {
  estimatedPrice: number;
}
