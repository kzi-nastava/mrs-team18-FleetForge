export interface VehicleProfileChangeRequest {
    id: number;
    firstName: string;
    lastName: string;
  oldModel: string;
    newModel: string;
    oldRegistrationNumber: string;
    newRegistrationNumber: string;
    oldType: "VAN" | "LUXURY" | "STANDARD";
    newType: "VAN" | "LUXURY" | "STANDARD";
    oldSpace: number;
    newSpace: number;
    oldBabySeat: boolean;
    newBabySeat: boolean;
    oldPetFriendly: boolean;
    newPetFriendly: boolean;
}