import { InformationChangeRequestStatus } from "../enums/information-change-request-status";

export interface DriverProfileChangePendingRequestsDTO{
    requestId:number;
    driverId:number;
    firstName:string;
    lastName:string;
    email:string;
    phoneNumber:string;
    address:string;

    newFirstName?:string;
    newLastName?:string;
    newEmail?:string;
    newPhoneNumber?:string;
    newAddress?:string;
}

export interface VehicleProfileChangePendingRequestsDTO{
    vehicleId:number;
    requestId:number;
    firstName:string;
    lastName:string;
    model:string;
    type:string;
    registrationNumber:string;
    space:number;
    babySeat:boolean;
    petFriendly:boolean;
    newModel?:string;
    newType?:string;
    newRegistrationNumber?:string;
    newSpace?:number;
    newBabySeat?:boolean;
    newPetFriendly?:boolean;
}

export interface ChangeAcceptanceDTO{
    change:boolean;
}
export interface ChangeAcceptanceResponseDTO{
    status:InformationChangeRequestStatus;
    id:number;
}