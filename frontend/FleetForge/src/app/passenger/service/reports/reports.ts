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

 getReport(fromDate: string, toDate: string): Observable<UserDataReportResponseDTO> {
   return this.http.get<UserDataReportResponseDTO>(`${this.apiUrl+"/logged-user/report-data"}?fromDate=${fromDate}&toDate=${toDate}`);
 }
}
