import { RideTrackingDTO } from "./ride-tracking.dtos";

export interface FinishRideResponseDTO {
  rideId: number;
  status: string;
  endTime: string;
  totalCost: number;
  message: string;
  driverAvailable: boolean;
  nextRide: RideTrackingDTO | null;
}
