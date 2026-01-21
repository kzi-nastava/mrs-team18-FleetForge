import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DriverSessionResponseDTO } from '../../../shared/dtos/driver.dtos';

@Injectable({
  providedIn: 'root',
})
export class Navbar {
  constructor(private http: HttpClient) {
  }
  
  private apiUrl = 'http://localhost:8080/api/drivers';

  goOnline():Observable<DriverSessionResponseDTO>{
    return this.http.post<DriverSessionResponseDTO>(this.apiUrl+"/online",{});
  }
  goOffline(req:DriverSessionResponseDTO):Observable<void>{
    return this.http.put<void>(this.apiUrl+"/offline", req);
  }
}
