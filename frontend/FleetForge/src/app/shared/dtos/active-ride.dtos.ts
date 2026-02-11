import { GeoPoint } from '../models/geopoint.model';

export interface ActiveRideDTO {
  rideId: number;
  driverFirstName: string;
  driverLastName: string;
  driverProfileImage: string;
  startAddress: string;
  endAddress: string;
  startTime: string;
  panicActivated: boolean;
  passengerCount: number;
}

export interface ActiveRideDetailsDTO {
  rideId: number;
  
  driverFirstName: string;
  driverLastName: string;
  driverPhoneNumber: string;
  driverProfileImage: string;
  
  currentLocation: GeoPoint;
  
  startLocation: GeoPoint;
  startAddress: string;
  endLocation: GeoPoint;
  endAddress: string;
  
  startTime: string;
  
  panicActivated: boolean;
  panicActivatedAt: string | null;
  
  passengers: PassengerDTO[];
  vehicleType: string;
}

export interface PassengerDTO {
  firstName: string;
  lastName: string;
  email: string;
}
