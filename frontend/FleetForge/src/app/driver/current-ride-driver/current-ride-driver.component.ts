import { Component, OnInit, OnDestroy, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CurrentRideComponent, ActionButton, CardInfo } from '../../shared/current-ride/current-ride.component';
import { RideTrackingDTO } from '../../shared/dtos/ride-tracking.dtos';
import { ConfirmationPopupComponent } from '../../shared/popups/confirmation-popup/confirmation-popup.component';
import { NotificationPopupComponent } from '../../shared/popups/popup-dialog/notification-popup.component';
import { RideService } from '../../shared/services/ride.service';

import { DriverCurrentRide } from '../../driver/service/driver-current-ride/driver-current-ride';
import { RideStartResponseDTO } from '../../shared/dtos/ride.dtos';

@Component({
  selector: 'app-current-ride-driver',
  standalone: true,
  imports: [CommonModule, CurrentRideComponent, FormsModule, ConfirmationPopupComponent, NotificationPopupComponent],
  templateUrl: './current-ride-driver.component.html',
  styleUrls: ['./current-ride-driver.component.css']
})
export class CurrentRideDriverComponent implements OnInit, OnDestroy {
  @ViewChild(CurrentRideComponent) currentRideComponent!: CurrentRideComponent;

  rideData: RideTrackingDTO | null = null;
  cardInfo: CardInfo | null = null;
  actionButtons: ActionButton[] = [];

  showSosConfirmPopup = false;

  showCancelConfirm = false;
  showCancelReasonPopup = false;
  cancelReason: string = '';

  notificationVisible = false;
  notificationTitle = '';
  notificationMessage = '';
  notificationSuccess = true;

  isLoading = false;

  private locationUpdateInterval: any;
  private routeCoordinates: Array<{latitude: number, longitude: number}> = [];
  private currentCoordinateIndex: number = 0;
  private rideStarted: boolean = false;


  constructor(
    private driverCurrentRideService: DriverCurrentRide,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private rideService: RideService,

  ) {
  }

  ngOnInit(): void {
    this.loadActiveRide();
    this.updateActionButtons();
  }

  ngOnDestroy(): void {
    this.clearTracking();
  }

  onRouteCoordinatesReceived(coords: Array<{ latitude: number; longitude: number }>): void {
    this.routeCoordinates = coords;
    this.currentCoordinateIndex = 0;
    this.resolveSimulationFlow();
  }

  private resolveSimulationFlow(): void {
    this.clearTracking();
    if (!this.rideData) return;

    if (this.rideData.status === 'ACCEPTED') {
      this.startApproachToPickupSimulation();
    }

    if (this.rideData.status === 'IN_PROGRESS') {
      this.startRideSimulation();
    }
  }

  private startApproachToPickupSimulation(): void {
    this.locationUpdateInterval = setInterval(() => {
      if (!this.rideData || this.routeCoordinates.length === 0) return;

      const targetIndex = Math.min(this.currentCoordinateIndex + 5, this.routeCoordinates.length - 1);
      const coord = this.routeCoordinates[targetIndex];

      this.updateLocationOnMap(coord);
      this.sendLocationUpdate(coord);
      this.currentCoordinateIndex = targetIndex;

      if (this.currentCoordinateIndex >= this.routeCoordinates.length - 1) {
        this.clearTracking();
      }
    }, 1000);
  }

  private startRideSimulation(): void {
    const waypointThreshold = 0.0005;

    this.locationUpdateInterval = setInterval(() => {
      if (!this.rideData || this.routeCoordinates.length === 0) return;

      const targetIndex = Math.min(this.currentCoordinateIndex + 5, this.routeCoordinates.length - 1);
      const coord = this.routeCoordinates[targetIndex];

      this.rideData.route.waypoints.forEach(wp => {
        if (!wp.isCompleted) {
          const latDiff = Math.abs(coord.latitude - wp.location.latitude);
          const lngDiff = Math.abs(coord.longitude - wp.location.longitude);
          if (latDiff < waypointThreshold && lngDiff < waypointThreshold) {
            wp.isCompleted = true;
          }
        }
      });

      this.updateLocationOnMap(coord);
      this.sendLocationUpdate(coord);
      this.currentCoordinateIndex = targetIndex;

      if (this.currentCoordinateIndex >= this.routeCoordinates.length - 1) {
        this.clearTracking();
        this.rideData.status = 'COMPLETED';
        console.log('Ride completed');
      }
    }, 1000);
  }

  private updateLocationOnMap(coord: { latitude: number; longitude: number }): void {
    if (!this.rideData) return;

    this.rideData.currentLocation.latitude = coord.latitude;
    this.rideData.currentLocation.longitude = coord.longitude;
    this.currentRideComponent?.mapComponent?.updateCurrentLocation(this.rideData.currentLocation);
  }

  private sendLocationUpdate(coord: { latitude: number; longitude: number }): void {
    if (!this.rideData) return;

    this.driverCurrentRideService.updateLocation({
      currentLocation: { latitude: coord.latitude, longitude: coord.longitude }
    }).subscribe();
  }

  private clearTracking(): void {
    if (this.locationUpdateInterval) {
      clearInterval(this.locationUpdateInterval);
    }
  }

  onActionButton(action: string): void {
    if (action === 'start-ride') this.onStartRide();
    if (action === 'finish-ride') this.onFinishRide();
    if (action === 'sos') {
      this.showSosConfirmPopup = true;
    }
    if (action === 'cancel') this.openCancelConfirmation();
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
      }
    });
  }

  closeSosPopup(): void {
    this.showSosConfirmPopup = false;
  }


  private openCancelConfirmation(): void {
    this.showCancelConfirm = true;
  }

  confirmCancelRide(): void {
    this.showCancelConfirm = false;

    this.showCancelReasonPopup = true;
  }

  submitCancelReason(): void {
    if (!this.rideData || !this.cancelReason.trim()) {
      return;
    }

    const reason = this.cancelReason.trim();

    this.showCancelReasonPopup = false;
    this.clearTracking();

    this.rideService.cancelRide(this.rideData.rideId, reason).subscribe({
      next: (response) => {
        this.rideData!.status = 'CANCELLED';
        this.cancelReason = '';

        this.notificationTitle = 'Ride Cancelled';
        this.notificationMessage = response.message;
        this.notificationSuccess = true;
        this.notificationVisible = true;

        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to cancel ride', err);

        this.notificationTitle = 'Cancellation Failed';
        this.notificationMessage =
          err?.error?.message ?? 'Unable to cancel the ride. Please try again.';
        this.notificationSuccess = false;
        this.notificationVisible = true;

        this.cdr.detectChanges();
      }
    });
  }


  onNotificationClosed(): void {
    this.notificationVisible = false;
  }

  closeCancelConfirmation(): void {
  this.showCancelConfirm = false;
  }

  closeCancelReasonPopup(): void {
    this.showCancelReasonPopup = false;
    this.cancelReason = '';
  }



  private onStartRide(): void {
    if (!this.rideStarted) {
      this.rideStarted = true;
      this.driverCurrentRideService.startRide(this.rideData!.rideId.toString()).subscribe({
        next: (response:RideStartResponseDTO) => {
          console.log('Ride started:', response);
        }
      });
      if (this.rideData) this.rideData.status = 'IN_PROGRESS';
      this.currentCoordinateIndex = 0;
      this.updateActionButtons();
      this.startRideSimulation();
      alert('Ride started');
    }
  }

  private onFinishRide(): void {
    if (!this.rideData) return;

    this.showCancelConfirm = false;
    this.isLoading = true;
    this.clearTracking();

    this.driverCurrentRideService.finishRide(this.rideData.rideId).subscribe({
      next: (response) => {
        console.log('Ride finished successfully:', response);
        this.isLoading = false;

        if (response.nextRide) {
          // Driver has next scheduled ride - load it
          alert(`Ride completed! Loading your next scheduled ride...`);
          console.log('Loading next scheduled ride:', response.nextRide.rideId);
          this.rideData = response.nextRide;
          this.setCardInfoFromRide();
          this.rideStarted = false;
          this.currentCoordinateIndex = 0;
          this.updateActionButtons();
          this.cdr.detectChanges();

          if (this.rideData?.currentLocation) {
            this.currentRideComponent?.mapComponent?.updateCurrentLocation(this.rideData.currentLocation);
          }
        } else {
          // No next ride - driver is available, redirect to home
          alert('Ride completed successfully! You are now available for new rides.');
          console.log('Driver is now available, redirecting to home');
          this.rideData = null;
          this.cardInfo = null;
          this.cdr.detectChanges();
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error finishing ride:', err);
        alert('Failed to finish ride. Please try again.');
        this.cdr.detectChanges();
      }
    });
  }

  private updateActionButtons(): void {
    this.actionButtons = this.rideStarted
      ? [
          { label: 'Finish Ride', color: 'primary', action: 'finish-ride' },
          { label: 'SOS', color: 'warn', action: 'sos' }
        ]
      : [
          { label: 'Start Ride', color: 'success', action: 'start-ride' },
          { label: 'Cancel', color: 'warn', action: 'cancel' }
        ];
  }

  private setCardInfoFromRide(): void {
    if (this.rideData && this.rideData.passenger) {
      const passenger = this.rideData.passenger;
      this.cardInfo = {
        label: 'Passenger',
        name: `${passenger.firstName} ${passenger.lastName}`,
        rating: 4.8,
        phoneNumber: passenger.phoneNumber,
        profileImage: passenger.profileImage
      };
    } else {
      this.cardInfo = null;
    }
  }

  private loadActiveRide(): void {
    this.isLoading = true;

    this.driverCurrentRideService.getActiveTracking()
      .subscribe({
        next: (data) => {
          this.rideData = data;

          this.setCardInfoFromRide();

          this.isLoading = false;

          this.cdr.detectChanges();

          if (this.rideData?.currentLocation) {
            this.currentRideComponent?.mapComponent?.updateCurrentLocation(this.rideData.currentLocation);
          }

          // Log again after change detection
          setTimeout(() => {
          }, 100);
        },
        error: (err) => {
          this.isLoading = false;
          if (err?.status === 404) {
            this.rideData = null;
            this.cardInfo = null;
          } else {
          }
          this.cdr.detectChanges();
        }
      });
  }
}
