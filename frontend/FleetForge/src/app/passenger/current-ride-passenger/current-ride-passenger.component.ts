import { Component, OnInit, OnDestroy, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CurrentRideComponent, ActionButton, CardInfo } from '../../shared/current-ride/current-ride.component';
import { RideTrackingDTO } from '../../shared/dtos/ride-tracking.dtos';
import { PassengerCurrentRide } from '../../passenger/service/passenger-current-ride/passenger-current-ride';
import { interval, Subscription, of } from 'rxjs';
import { catchError, startWith, switchMap } from 'rxjs/operators';
import { RideService } from '../../shared/services/ride.service';
import { RideReviewService } from '../service/passenger-ride-review/ride-review.service';
import { ConfirmationPopupComponent } from '../../shared/popups/confirmation-popup/confirmation-popup.component';
import { NotificationPopupComponent } from '../../shared/popups/popup-dialog/notification-popup.component';
import { RideRatingModalComponent, RatingFormData } from '../../shared/popups/ride-rating-modal/ride-rating-modal.component';


@Component({
  selector: 'app-current-ride-passenger',
  standalone: true,
  imports: [CommonModule, CurrentRideComponent, FormsModule, ConfirmationPopupComponent, NotificationPopupComponent, RideRatingModalComponent],
  templateUrl: './current-ride-passenger.component.html',
  styleUrls: ['./current-ride-passenger.component.css']
})
export class CurrentRidePassengerComponent implements OnInit, OnDestroy {
  @ViewChild(CurrentRideComponent) currentRideComponent!: CurrentRideComponent;
  
  rideData: RideTrackingDTO | null = null;
  cardInfo: CardInfo | null = null;
  actionButtons: ActionButton[] = [
    { label: 'Wrong way', color: 'primary', action: 'wrong-way' },
    { label: 'SOS', color: 'warn', action: 'sos' }
  ];
  
  private trackingSub?: Subscription;
  
  showWrongWayModal: boolean = false;
  wrongWayReport: string = '';

  showSosConfirmPopup = false;

  notificationVisible = false;
  notificationTitle = '';
  notificationMessage = '';
  notificationSuccess = true;

  showRatingModal = false;
  isRatingLoading = false;
  ratingForm: RatingFormData = {
    driverRating: 0,
    vehicleRating: 0,
    comment: '',
  };
  rideHasBeenRated = false;
  
  constructor(
    private passengerCurrentRideService: PassengerCurrentRide,
    private rideService: RideService,
    private rideReviewService: RideReviewService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}
    
  ngOnInit(): void {
    this.startTrackingPoll();
  }

  ngOnDestroy(): void {
    this.clearTracking();
  }

  private clearTracking() {
    if (this.trackingSub) {
      this.trackingSub.unsubscribe();
      this.trackingSub = undefined;
    }
  }

  get activeActionButtons(): ActionButton[] {
    if (this.isInProgress()) {
      return this.actionButtons;
    }
    return [];
  }

  private setCardInfoFromRide(): void {
    if (this.rideData && this.rideData.driver) {
      const driver = this.rideData.driver;
      this.cardInfo = {
        label: 'Driver',
        name: `${driver.firstName} ${driver.lastName}`,
        rating: 4.8,
        phoneNumber: driver.phoneNumber,
        profileImage: driver.profileImage
      };
    } else {
      this.cardInfo = null;
    }
  }

  private startTrackingPoll(): void {
    this.trackingSub = interval(1500)
      .pipe(
        startWith(0),
        switchMap(() => this.passengerCurrentRideService.getActiveTracking()
          .pipe(
            catchError(err => {
              // If no active ride (404), clear state; keep polling.
              if (err?.status === 404) {
                this.rideData = null;
                this.cardInfo = null;
                this.rideService.setActiveRide(null);
                this.clearTracking();
                this.cdr.detectChanges();
              }
              return of(null);
            })
          )
        )
      )
      .subscribe((data) => {
        if (!data) {
          this.rideService.setActiveRide(null);
          return;
        }
        this.rideData = data as RideTrackingDTO;
        this.rideService.setActiveRide(this.rideData);
        this.setCardInfoFromRide();

        this.cdr.detectChanges();

        if (this.rideData?.currentLocation) {
          this.currentRideComponent?.mapComponent?.updateCurrentLocation(this.rideData.currentLocation);
        }
        
        if (this.isCompleted() && !this.rideHasBeenRated) {
          this.trackingSub?.unsubscribe();
          this.trackingSub = undefined;
          this.showRatingModal = true;
        }
      });
  }

  onActionButton(action: string): void {
    if (action === 'wrong-way') {
      this.showWrongWayModal = true;
    }
    if (action === 'sos') {
      this.showSosConfirmPopup = true;
    }
  }

  confirmSos(): void {
    if (!this.rideData?.rideId) return;

    const rideId = this.rideData.rideId;

    this.rideService.triggerPanic(rideId).subscribe({
      next: (response) => {
        this.notificationTitle = 'SOS Activated';
        this.notificationMessage = response.message;
        this.notificationSuccess = true;
        this.notificationVisible = true;

        this.closeSosPopup();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.notificationTitle = 'SOS Failed';
        this.notificationMessage =
          err?.error?.message ?? 'Unable to activate SOS.';
        this.notificationSuccess = false;
        this.notificationVisible = true;

        this.closeSosPopup();
        this.cdr.detectChanges();
      },
    });
  }

  closeSosPopup(): void {
    this.showSosConfirmPopup = false;
  }

  onNotificationClosed(): void {
    this.notificationVisible = false;
  }



  onCloseWrongWayModal(): void { this.showWrongWayModal = false; }
  
  onSubmitWrongWayReport(): void {
    if (this.wrongWayReport.trim()) this.onCloseWrongWayModal();
  }

  hasDriver(): boolean {
    return !!this.rideData?.driver;
  }

  isAccepted(): boolean {
    return this.rideData?.status === 'ACCEPTED';
  }

  isInProgress(): boolean {
    return this.rideData?.status === 'IN_PROGRESS';
  }

  isCompleted(): boolean {
    return this.rideData?.status === 'COMPLETED';
  }

  getRideSubtitle(): string {
    if (!this.rideData) return '';

    if (this.isAccepted() && !this.hasDriver()) {
      return 'We will assign you a driver in a moment';
    }

    if (this.isAccepted() && this.hasDriver()) {
      return 'Driver is coming to your pickup location';
    }

    if (this.isInProgress()) {
      return 'Enjoy your ride — live tracking enabled';
    }

    if (this.isCompleted()) {
      return 'Ride completed';
    }

    return '';
  }

  closeRatingModal(): void {
    this.showRatingModal = false;
    this.ratingForm = { driverRating: 0, vehicleRating: 0, comment: '' };
  }

  onRatingSubmitted(formData: RatingFormData): void {
    if (!this.rideData?.rideId) return;

    this.isRatingLoading = true;

    const driverRating = Math.min(Math.max(formData.driverRating, 1), 5);
    const vehicleRating = Math.min(Math.max(formData.vehicleRating, 1), 5);

    this.rideReviewService
      .createReview(this.rideData.rideId, {
        driverRating,
        vehicleRating,
        comment: formData.comment.trim(),
      })
      .subscribe({
        next: (response) => {
          this.isRatingLoading = false;
          this.rideHasBeenRated = true;
          this.showRatingModal = false;
          this.navigateToDashboard();
        },
        error: (err) => {
          console.error('Failed to submit rating:', err);
          this.isRatingLoading = false;
        },
      });
  }

  onRatingNotNow(): void {
    this.rideHasBeenRated = true;
    this.showRatingModal = false;
    this.navigateToDashboard();
  }

  private navigateToDashboard(): void {
    this.router.navigate(['/passenger/dashboard']);
  }
}