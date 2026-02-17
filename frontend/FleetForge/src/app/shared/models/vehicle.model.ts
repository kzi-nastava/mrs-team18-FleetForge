export interface GeoPoint {
  latitude: number;
  longitude: number;
}

export enum VehicleType {
  STANDARD = 'STANDARD',
  LUXURY = 'LUXURY',
  VAN = 'VAN'
}

export interface VehicleLocationDTO {
  vehicleId: number;
  model: string;
  vehicleType: VehicleType;
  currentLocation: GeoPoint;
  isAvailable: boolean;
  isActive: boolean;
  panicActivated: boolean;
  rideId: number | null;
}
