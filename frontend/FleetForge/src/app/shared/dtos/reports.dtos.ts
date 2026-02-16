import { RideReportDataDTO } from "./ride.dtos";

export interface UserDataReportResponseDTO{
    dataByDay: Map<Date, RideReportDataDTO[]>;
}