import { VehicleType } from "./vehicle.dtos";

export interface RideCreateRequestDTO{
    coordinates: WayPointDTO[];
    passengerNumber: number;
    rideTime:string;
    rideNow: boolean;
    passengerEmails: string[];
    vehicleType:VehicleType;
    babySeat: boolean;
    petFriendly: boolean;
    startAddress: string;
    endAddress: string;
    totalDistance: number;
    estimatedDuration:number;
}

export interface WayPointDTO{
    location: GeoPoint;
    address: string;
    orderIndex: number;
}

export interface GeoPoint{
    latitude: number;
    longitude: number;
}

export interface RideCreateResponseDTO{
    created:boolean;
}

export interface RideFavoriteRoutesDTO{
    id:number;
    rideId:number;
    startAddress:string;
    endAddress:string;
    waypoints:WayPointDTO[];
    name:string;
}

export interface RideStartResponseDTO{
    id:number;
    status:string;
}

export interface RideReviewRequestDTO {
    driverRating: number;
    vehicleRating: number;
    comment?: string;
}

export interface RideReviewResponseDTO {
    rideId: number;
    driverRating: number;
    vehicleRating: number;
    comment: string;
    reviewedAt: string;
}