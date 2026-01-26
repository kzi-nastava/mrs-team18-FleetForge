import { GeoPoint } from "../models/vehicle.model";

export interface DriverLocationUpdateRequestDTO {
  currentLocation: GeoPoint;
}

export interface DriverLocationUpdateResponseDTO {
  message: string;
  updatedAt: string; 
}
