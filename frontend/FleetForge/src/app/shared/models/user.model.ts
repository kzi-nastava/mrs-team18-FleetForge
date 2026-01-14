export interface User{
    id:number;
    firstName:string;
    lastName:string;
    email:string;
    number:string;
    homeAddress:string;
    profilePictureUrl?: string;
}

export interface Passenger extends User{
    //additional attributes can be added later
}
export interface Driver extends User{
    //additional attributes can be added later
}
export interface Admin extends User{
    //additional attributes can be added later
}