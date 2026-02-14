import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { RideReviewService } from './ride-review.service';
import { RideReviewRequestDTO, RideReviewResponseDTO } from '../../../shared/dtos/ride.dtos';

describe('RideReviewService', () => {
  let service: RideReviewService;
  let httpMock: HttpTestingController;
  const baseUrl = 'http://localhost:8080/api/rides';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RideReviewService,
      ],
    });
    service = TestBed.inject(RideReviewService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('createReview', () => {
    it('should POST review to correct endpoint', () => {
      const rideId = 1;
      const reviewData: RideReviewRequestDTO = {
        driverRating: 5,
        vehicleRating: 4,
        comment: 'Great service!',
      };
      const mockResponse: RideReviewResponseDTO = {
        rideId,
        driverRating: 5,
        vehicleRating: 4,
        comment: 'Great service!',
        reviewedAt: new Date().toISOString(),
      };

      service.createReview(rideId, reviewData).subscribe((response) => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${baseUrl}/${rideId}/review`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(reviewData);
      req.flush(mockResponse);
    });

    it('should handle server error when creating review', () => {
      const rideId = 1;
      const reviewData: RideReviewRequestDTO = {
        driverRating: 5,
        vehicleRating: 4,
        comment: 'Test',
      };

      service.createReview(rideId, reviewData).subscribe(
        () => fail('should have failed'),
        (error) => {
          expect(error.status).toBe(400);
        }
      );

      const req = httpMock.expectOne(`${baseUrl}/${rideId}/review`);
      req.flush({ error: 'Invalid rating' }, { status: 400, statusText: 'Bad Request' });
    });

    it('should send empty comment if not provided', () => {
      const rideId = 1;
      const reviewData: RideReviewRequestDTO = {
        driverRating: 5,
        vehicleRating: 4,
      };

      service.createReview(rideId, reviewData).subscribe();

      const req = httpMock.expectOne(`${baseUrl}/${rideId}/review`);
      expect(req.request.body.comment).toBeUndefined();
      req.flush({
        rideId,
        driverRating: 5,
        vehicleRating: 4,
        comment: '',
        reviewedAt: new Date().toISOString(),
      });
    });
  });


});
