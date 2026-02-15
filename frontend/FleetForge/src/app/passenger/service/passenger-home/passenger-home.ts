import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RideCreateRequestDTO, RideCreateResponseDTO } from '../../../shared/dtos/ride.dtos';
import { Observable } from 'rxjs';
import { GetIsBlockedUserDTO } from '../../../shared/dtos/users.dtos';

@Injectable({
  providedIn: 'root',
})
export class PassengerHome {
  private apiUrl = 'http://localhost:8080/api/rides';
  private usersUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {
  }

  createRide(req:RideCreateRequestDTO):Observable<RideCreateResponseDTO>{
    return this.http.post<RideCreateResponseDTO>(this.apiUrl+"/create", req);
  }
  getIsBlocked():Observable<GetIsBlockedUserDTO>{
    return this.http.get<GetIsBlockedUserDTO>(this.usersUrl+"/blocked");
  }
}
