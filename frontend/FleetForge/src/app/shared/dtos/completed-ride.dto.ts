export interface CompletedRideDTO {
  passengerName: string;
  id: number;
  pickupAddress: string;
  dropoffAddress: string;
  rideDate: string; // Format: YYYY-MM-DD
  totalCost: number;
  cancelledBy: string | null; 
  panicActivation: boolean;
  feedback: number; // 0-5 stars, 0 means waiting
  linkedPassengers: string[];
  pickupCoords: [number, number]; // [latitude, longitude]
  dropoffCoords: [number, number]; // [latitude, longitude]
  waypoints: [number, number][]; 
}