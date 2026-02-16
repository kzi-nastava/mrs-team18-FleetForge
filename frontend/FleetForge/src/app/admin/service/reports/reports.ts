import { Injectable } from '@angular/core';
import { UserDataReportResponseDTO } from '../../../shared/dtos/reports.dtos';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class Reports {
 private apiUrl = 'http://localhost:8080/api/reports'; 
 constructor(private http: HttpClient) {}

 getAdminReportUser(fromDate: string, toDate: string, email: string): Observable<UserDataReportResponseDTO> {
   return this.http.get<UserDataReportResponseDTO>(`${this.apiUrl+"/admin-by-user/report-data"}?fromDate=${fromDate}&toDate=${toDate}&email=${email}`);
 }
 getAdminReportForAll(fromDate: string, toDate: string): Observable<UserDataReportResponseDTO> {
   return this.http.get<UserDataReportResponseDTO>(`${this.apiUrl+"/admin/report-data"}?fromDate=${fromDate}&toDate=${toDate}`);
 }
}
