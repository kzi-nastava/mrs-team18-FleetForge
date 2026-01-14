
import { InformationChangeRequestStatus } from "../enums/information-change-request-status";
import { VehicleInformationDTO } from "./vehicle.dtos";

export interface UserInformationDTO{
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
    id:number;
    newFirstName?:string;
    newLastName?:string;
    newEmail?:string;
    newPhoneNumber?:string;
    newAddress?:string;
    newProfilePicture?:string;
}
export interface DriverChangeResponseDTO{
    status:InformationChangeRequestStatus;
    createdAt:Date;
    requestId:number;
}

export interface PasswordChangeDTO{
    newPassword:string;
}