import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PassengerHistory {
  private apiUrl = 'http://localhost:8080/api/passenger/';

  constructor(private http: HttpClient) { }

  addFavoriteRoute(name:string, rideId:number) {
    return this.http.post<void>(this.apiUrl + `favorites/${name}/${rideId}`,{});
  }
   deleteFavoriteRoute(routeId:number):Observable<void> {
      return this.http.delete<void>(this.apiUrl + `favorites/${routeId}`);
    }
}
