import { Driver } from "../models/user.model";
import { VehicleCreateRequestDTO } from "./vehicle.dtos";

export interface DriverCreateRequestDTO{
    firstName:string;
    lastName:string;
    email:string;
    phoneNumber:string;
    address:string;
    vehicle: VehicleCreateRequestDTO;
}

export interface DriverCreateResponseDTO{
    driver:Driver;
}

export interface PasswordSetDTO{
    token:string;
    password:string;
}

export interface PasswordSetResponseDTO{
    success:boolean;
}

export interface DriverSessionResponseDTO{
    sessionId:number;
}
