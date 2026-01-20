import { GeoPoint } from "../models/vehicle.model";

export interface WaypointDTO {
  location: GeoPoint;
  address: string;
  order: number;
}

export interface RouteInfoDTO {
  startLocation: GeoPoint;
  startAddress: string;
  endLocation: GeoPoint;
  endAddress: string;
  waypoints: WaypointDTO[];
  totalDistanceKm: number;
}

export interface DriverInfoDTO {
  id: number;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  profileImage: string;
}

export interface RideTrackingDTO {
  rideId: number;
  status: string;
  currentLocation: GeoPoint;
  estimatedArrivalMinutes: number;
  route: RouteInfoDTO;
  driver: DriverInfoDTO;
  panicActivated: boolean;
}
