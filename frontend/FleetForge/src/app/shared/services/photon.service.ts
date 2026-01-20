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
      map(response => {
        if (response.features) {
          return response.features.map(feature => ({
            ...feature,
            properties: {
              ...feature.properties,
              name: this.transliterateIfNeeded(feature.properties.name),
              street: this.transliterateIfNeeded(feature.properties.street),
              city: this.transliterateIfNeeded(feature.properties.city)
            }
          }));
        }
        return [];
      })
    );
  }

  private transliterateIfNeeded(text: string | undefined): string | undefined {
    if (!text) return text;
    
    // Check if text contains Cyrillic characters
    if (!/[\u0400-\u04FF]/.test(text)) {
      return text; // Already Latin
    }
    
    const translitMap: { [key: string]: string } = {
      'А': 'A', 'а': 'a',
      'Б': 'B', 'б': 'b',
      'В': 'V', 'в': 'v',
      'Г': 'G', 'г': 'g',
      'Д': 'D', 'д': 'd',
      'Ђ': 'Đ', 'ђ': 'đ',
      'Е': 'E', 'е': 'e',
      'Ж': 'Ž', 'ж': 'ž',
      'З': 'Z', 'з': 'z',
      'И': 'I', 'и': 'i',
      'Ј': 'J', 'ј': 'j',
      'К': 'K', 'к': 'k',
      'Л': 'L', 'л': 'l',
      'Љ': 'Lj', 'љ': 'lj',
      'М': 'M', 'м': 'm',
      'Н': 'N', 'н': 'n',
      'Њ': 'Nj', 'њ': 'nj',
      'О': 'O', 'о': 'o',
      'П': 'P', 'п': 'p',
      'Р': 'R', 'р': 'r',
      'С': 'S', 'с': 's',
      'Т': 'T', 'т': 't',
      'Ћ': 'Ć', 'ћ': 'ć',
      'У': 'U', 'у': 'u',
      'Ф': 'F', 'ф': 'f',
      'Х': 'H', 'х': 'h',
      'Ц': 'C', 'ц': 'c',
      'Ч': 'Č', 'ч': 'č',
      'Џ': 'Dž', 'џ': 'dž',
      'Ш': 'Š', 'ш': 'š'
    };
    
    return text.split('').map(char => translitMap[char] || char).join('');
  }
}