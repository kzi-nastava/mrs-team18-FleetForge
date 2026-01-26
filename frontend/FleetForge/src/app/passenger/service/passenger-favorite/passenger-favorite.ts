import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RideFavoriteRoutesDTO } from '../../../shared/dtos/ride.dtos';

@Injectable({
  providedIn: 'root',
})
export class PassengerFavorite {

  private apiUrl = 'http://localhost:8080/api/passenger/';
  constructor(private http: HttpClient) { }

  getFavoriteRoutes():Observable<RideFavoriteRoutesDTO[]> {
    return this.http.get<RideFavoriteRoutesDTO[]>(this.apiUrl + 'favorites');
  }
  deleteFavoriteRoute(routeId:number):Observable<void> {
    return this.http.delete<void>(this.apiUrl + `favorites/${routeId}`);
  }
}
