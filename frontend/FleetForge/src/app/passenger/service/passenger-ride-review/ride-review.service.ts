import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RideReviewRequestDTO, RideReviewResponseDTO } from '../../../shared/dtos/ride.dtos';

@Injectable({
  providedIn: 'root',
})
export class RideReviewService {
  private apiUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {}

  /**
   * Create a review for a completed ride
   * POST /api/rides/{rideId}/review
   */
  createReview(rideId: number, request: RideReviewRequestDTO): Observable<RideReviewResponseDTO> {
    return this.http.post<RideReviewResponseDTO>(
      `${this.apiUrl}/${rideId}/review`,
      request
    );
  }

  /**
   * Check if a passenger can review a ride
   * GET /api/rides/{rideId}/can-review
   */
  canReviewRide(rideId: number): Observable<{ canReview: boolean }> {
    return this.http.get<{ canReview: boolean }>(
      `${this.apiUrl}/${rideId}/can-review`
    );
  }
}
