import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface PhotonFeature {
  type: string;
  properties: {
    osm_type: string;
    osm_id: number;
    name?: string;
    street?: string;
    housenumber?: string;
    postcode?: string;
    city?: string;
    district?: string;
    state?: string;
    country?: string;
    countrycode?: string;
  };
  geometry: {
    type: string;
    coordinates: [number, number]; // [longitude, latitude]
  };
}

export interface PhotonResponse {
  type: string;
  features: PhotonFeature[];
}

@Injectable({
  providedIn: 'root',
})
export class PhotonService {
  private baseUrl = 'https://photon.komoot.io';

  constructor(private http: HttpClient) {}

  searchSuggestions(query: string): Observable<PhotonFeature[]> {
    return this.http.get<PhotonResponse>(`${this.baseUrl}/api`, {
      params: {
        q: query,
        lat: '45.2671', // Novi Sad latitude
        lon: '19.8335', // Novi Sad longitude
        limit: '5',
        lang: 'en',
        bbox: '19.7639,45.2212,19.9316,45.3219' // left,bottom,right,top
      }
    }).pipe(
      map(response => response.features || [])
    );
  }
}