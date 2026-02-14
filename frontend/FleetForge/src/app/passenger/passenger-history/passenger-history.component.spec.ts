import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PassengerHistoryComponent } from './passenger-history.component';
import { PassengerHistory } from '../service/passenger-history/passenger-history';
import { PassengerFavorite } from '../service/passenger-favorite/passenger-favorite';
import { RideReviewService } from '../service/passenger-ride-review/ride-review.service';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import { of, throwError } from 'rxjs';
import { RideReviewResponseDTO } from '../../shared/dtos/ride.dtos';

describe('PassengerHistoryComponent - Ride Rating Functionality', () => {
  let component: PassengerHistoryComponent;
  let fixture: ComponentFixture<PassengerHistoryComponent>;
  let mockPassengerHistory: jasmine.SpyObj<PassengerHistory>;
  let mockPassengerFavorite: jasmine.SpyObj<PassengerFavorite>;
  let mockRideReviewService: jasmine.SpyObj<RideReviewService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockChangeDetectorRef: jasmine.SpyObj<ChangeDetectorRef>;

  const mockRide = {
    id: 1,
    pickupAddress: 'Kozacinskog 1',
    dropoffAddress: 'Sekspirova 2',
    startDate: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
    endDate: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000 + 3600).toISOString(),
    status: 'COMPLETED' as const,
  };

  const mockRatedRide = {
    ...mockRide,
    id: 2,
    driverRating: 5,
    vehicleRating: 4,
    ratingComment: 'Great ride!',
  };

  const mockExpiredRide = {
    ...mockRide,
    id: 3,
    startDate: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
    endDate: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000 + 3600000).toISOString(),
  };

  const mockCancelledRide = {
    ...mockRide,
    id: 4,
    status: 'CANCELLED' as const,
  };

  const mockReviewResponse: RideReviewResponseDTO = {
    rideId: 1,
    driverRating: 5,
    vehicleRating: 4,
    comment: 'Excellent service',
    reviewedAt: new Date().toISOString(),
  };

  beforeEach(async () => {
    mockPassengerHistory = jasmine.createSpyObj('PassengerHistory', [
      'getPassengerRides', 
    ]);
    mockPassengerFavorite = jasmine.createSpyObj('PassengerFavorite', [
      'getFavoriteRoutes',
    ]);
    mockRideReviewService = jasmine.createSpyObj('RideReviewService', [
      'createReview', 
    ]);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockChangeDetectorRef = jasmine.createSpyObj('ChangeDetectorRef', [
      'markForCheck', 
    ]);

    mockPassengerHistory.getPassengerRides.and.returnValue(
      of({
        content: [],
        totalPages: 0,
        totalElements: 0,
        size: 5,
        number: 0,
        first: true,
        last: true,
      })
    );
    mockPassengerFavorite.getFavoriteRoutes.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [PassengerHistoryComponent],
      providers: [
        { provide: PassengerHistory, useValue: mockPassengerHistory },
        { provide: PassengerFavorite, useValue: mockPassengerFavorite },
        { provide: RideReviewService, useValue: mockRideReviewService },
        { provide: Router, useValue: mockRouter },
        { provide: ChangeDetectorRef, useValue: mockChangeDetectorRef },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PassengerHistoryComponent);
    component = fixture.componentInstance;
  });

  describe('Opening Rating Modal', () => {
    let testRide: any;
    let testRatedRide: any;
    let testExpiredRide: any;
    let testCancelledRide: any;

    beforeEach(() => {
      testRide = { ...mockRide };
      testRatedRide = { ...mockRatedRide };
      testExpiredRide = { ...mockExpiredRide };
      testCancelledRide = { ...mockCancelledRide };
    });

    it('should open rating modal when clicking rate button for pending ride', () => {
      component.openRating(testRide);

      expect(component.isRatingModalOpen).toBe(true);
      expect(component.selectedRide).toEqual(testRide);
      expect(component.ratingForm).toEqual({
        driverRating: 0,
        vehicleRating: 0,
        comment: '',
      });
    });

    it('should not open rating modal for already rated ride', () => {
      component.openRating(testRatedRide);

      expect(component.isRatingModalOpen).toBe(false);
      expect(component.selectedRide).toBeNull();
    });

    it('should not open rating modal for expired ride', () => {
      component.openRating(testExpiredRide);

      expect(component.isRatingModalOpen).toBe(false);
      expect(component.selectedRide).toBeNull();
    });

    it('should not open rating modal for cancelled ride', () => {
      component.openRating(testCancelledRide);

      expect(component.isRatingModalOpen).toBe(false);
      expect(component.selectedRide).toBeNull();
    });
  });

  describe('Closing Rating Modal', () => {
    let testRide: any;

    beforeEach(() => {
      testRide = { ...mockRide };
    });

    it('should close rating modal and reset form', () => {
      component.selectedRide = testRide;
      component.isRatingModalOpen = true;
      component.ratingForm = {
        driverRating: 4,
        vehicleRating: 3,
        comment: 'Test',
      };

      component.closeRatingModal();

      expect(component.isRatingModalOpen).toBe(false);
      expect(component.selectedRide).toBeNull();
      expect(component.ratingForm).toEqual({
        driverRating: 0,
        vehicleRating: 0,
        comment: '',
      });
    });
  });

  describe('Submitting Rating', () => {
    let testRide: any;

    beforeEach(() => {
      testRide = { ...mockRide };
      component.selectedRide = testRide;
      component.isRatingModalOpen = true;
    });

    it('should submit rating with valid form data', () => {
      mockRideReviewService.createReview.and.returnValue(
        of(mockReviewResponse)
      );

      const formData = {
        driverRating: 5,
        vehicleRating: 4,
        comment: 'Excellent service',
      };

      component.submitRating(formData);

      expect(mockRideReviewService.createReview).toHaveBeenCalledWith(
        testRide.id,
        {
          driverRating: 5,
          vehicleRating: 4,
          comment: 'Excellent service',
        }
      );
    });

    it('should update ride with rating data on successful submission', (done) => {
      mockRideReviewService.createReview.and.returnValue(
        of(mockReviewResponse)
      );

      const formData = {
        driverRating: 5,
        vehicleRating: 4,
        comment: 'Excellent service',
      };
      const rideRef = component.selectedRide;

      component.submitRating(formData);

      setTimeout(() => {
        expect(rideRef?.driverRating).toBe(5);
        expect(rideRef?.vehicleRating).toBe(4);
        expect(rideRef?.ratingComment).toBe('Excellent service');
        done();
      }, 0);
    });

    it('should close modal and reset form after successful submission', () => {
      mockRideReviewService.createReview.and.returnValue(
        of(mockReviewResponse)
      );

      const formData = {
        driverRating: 5,
        vehicleRating: 4,
        comment: 'Test',
      };

      component.submitRating(formData);

      expect(component.isRatingModalOpen).toBe(false);
      expect(component.selectedRide).toBeNull();
      expect(component.ratingForm).toEqual({
        driverRating: 0,
        vehicleRating: 0,
        comment: '',
      });
    });

    it('should handle submission error and keep modal open', () => {
      const errorResponse = { status: 400, message: 'Invalid rating' };
      mockRideReviewService.createReview.and.returnValue(
        throwError(() => errorResponse)
      );

      spyOn(console, 'error');

      const formData = {
        driverRating: 5,
        vehicleRating: 4,
        comment: 'Test',
      };

      component.submitRating(formData);

      expect(component.isRatingLoading).toBe(false);
      expect(console.error).toHaveBeenCalledWith(
        'Failed to submit rating:',
        errorResponse
      );
      expect(component.isRatingModalOpen).toBe(true);
    });

    it('should handle empty comment submission', () => {
      mockRideReviewService.createReview.and.returnValue(
        of(mockReviewResponse)
      );

      const formData = {
        driverRating: 5,
        vehicleRating: 4,
        comment: '',
      };

      component.submitRating(formData);

      expect(mockRideReviewService.createReview).toHaveBeenCalledWith(
        testRide.id,
        jasmine.objectContaining({
          comment: '',
        })
      );
    });
  });

  describe('Integration: Complete Rating Flow', () => {
    let testRide: any;
    let testRatedRide: any;

    beforeEach(() => {
      testRide = { ...mockRide };
      testRatedRide = { ...mockRatedRide, driverRating: 5, vehicleRating: 4, ratingComment: 'Already rated' };
    });

    it('should complete full rating flow from opening modal to successful submission', (done) => {
      const ratingState = component.getRatingState(testRide);
      expect(ratingState).toBe('pending');

      component.openRating(testRide);
      expect(component.isRatingModalOpen).toBe(true);

      mockRideReviewService.createReview.and.returnValue(
        of(mockReviewResponse)
      );

      const formData = {
        driverRating: 5,
        vehicleRating: 4,
        comment: 'Great experience!',
      };

      component.submitRating(formData);
      expect(mockRideReviewService.createReview).toHaveBeenCalledWith(
        testRide.id,
        {
          driverRating: 5,
          vehicleRating: 4,
          comment: 'Great experience!',
        }
      );
      setTimeout(() => {
        expect(component.isRatingModalOpen).toBe(false);
        expect(component.selectedRide).toBeNull();
        done();
      }, 0);
    });

    it('should not allow re-rating of already rated rides', () => {
      const ratingState = component.getRatingState(testRatedRide);
      expect(ratingState).toBe('rated');

      component.openRating(testRatedRide);

      expect(component.isRatingModalOpen).toBe(false);
      expect(component.selectedRide).toBeNull();
    });
  });
});
