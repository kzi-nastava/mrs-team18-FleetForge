import { VehicleType } from "../models/vehicle.model";

export interface VehicleInformationDTO{
    model:string;
        type:VehicleType;
        registrationNumber:string;
        space:number;
        babySeat:boolean;
        petFriendly:boolean;
}

export interface VehicleChangeRequestDTO{
    vehicleId:number;
    newModel?:string;
    newType?:VehicleType;
    newRegistrationNumber?:string;
    newSpace?:number;
    newBabySeat?:boolean;
    newPetFriendly?:boolean;
}

export interface VehicleChangeResponseDTO{
    status:string;
    createdAt:Date;
    requestId:number;
}

export { VehicleType };
