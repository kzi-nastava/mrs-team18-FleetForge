export interface Vehicle{
    id:number;
    model:string;
    type:'standard' | 'luxury' | 'suv';
    licensePlateNumber:string;
    passengerNumber:number,
    isBabySeatAvailable:boolean;
    isPetFriendly:boolean;
}