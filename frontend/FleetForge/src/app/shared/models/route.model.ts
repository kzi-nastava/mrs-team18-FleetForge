import { ListRange } from "@angular/cdk/collections";
import { GeoPoint } from "./vehicle.model";

export interface Route{
    id:number;
    geometry:GeoPoint[];
    distanceMeters:number;
    durationSeconds:number;
}