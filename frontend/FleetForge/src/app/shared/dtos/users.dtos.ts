
import { InformationChangeRequestStatus } from "../enums/information-change-request-status";
import { VehicleInformationDTO } from "./vehicle.dtos";

export interface UserInformationDTO{
    id:number;
    firstName:string;
    lastName:string;
    email:string;
    phoneNumber:string;
    address:string;
    profilePicture:string;
}

export interface DriverInformationDTO extends UserInformationDTO{
    vehicle:VehicleInformationDTO;
}

export interface DriverChangeRequestDTO{
    newFirstName?:string;
    newLastName?:string;
    newEmail?:string;
    newPhoneNumber?:string;
    newAddress?:string;
}
export interface DriverChangeResponseDTO{
    status:InformationChangeRequestStatus;
    createdAt:Date;
    requestId:number;
}

export interface PasswordChangeDTO{
    newPassword:string;
}

export interface UserInformationChangeDTO{
    firstName:string;
    lastName:string;
    email:string;
    phoneNumber:string;
    address:string;
}