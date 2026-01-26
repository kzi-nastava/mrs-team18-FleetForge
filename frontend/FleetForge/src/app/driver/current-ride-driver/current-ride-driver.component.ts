import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CurrentRideComponent, ActionButton, CardInfo } from '../../shared/current-ride/current-ride.component';
import { RideTrackingDTO } from '../../shared/dtos/ride-tracking.dtos';
import { ConfirmationPopupComponent } from '../../shared/popups/confirmation-popup/confirmation-popup.component';
import { RideService } from '../../shared/services/ride.service';


@Component({
  selector: 'app-current-ride-driver',
  standalone: true,
  imports: [CommonModule, CurrentRideComponent, FormsModule, ConfirmationPopupComponent],
  templateUrl: './current-ride-driver.component.html',
  styleUrls: ['./current-ride-driver.component.css']
})
export class CurrentRideDriverComponent implements OnInit, OnDestroy {
  @ViewChild(CurrentRideComponent) currentRideComponent!: CurrentRideComponent;
  
  rideData: RideTrackingDTO | null = null;
  cardInfo: CardInfo | null = null;
  actionButtons: ActionButton[] = [];

  showCancelConfirm = false;  
  showCancelReasonPopup = false;
  cancelReason: string = '';    
  
  private locationUpdateInterval: any;
  private routeCoordinates: Array<{latitude: number, longitude: number}> = [];
  private currentCoordinateIndex: number = 0;
  private rideStarted: boolean = false;

  constructor(private rideService: RideService) {}
  
  ngOnInit(): void {
    this.loadMockData();
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
      this.currentCoordinateIndex = targetIndex;

      if (this.currentCoordinateIndex >= this.routeCoordinates.length - 1) {
        this.clearTracking();
        console.log('Driver arrived at pickup location');
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
      this.currentCoordinateIndex = targetIndex;

      if (this.currentCoordinateIndex >= this.routeCoordinates.length - 1) {
        this.clearTracking();
        this.rideData.status = 'COMPLETED';
        console.log('Ride completed');
      }
    }, 2000);
  }

  private updateLocationOnMap(coord: { latitude: number; longitude: number }): void {
    if (!this.rideData) return;

    this.rideData.currentLocation.latitude = coord.latitude;
    this.rideData.currentLocation.longitude = coord.longitude;
    this.currentRideComponent?.mapComponent?.updateCurrentLocation(this.rideData.currentLocation);
  }

  private clearTracking(): void {
    if (this.locationUpdateInterval) {
      clearInterval(this.locationUpdateInterval);
    }
  }

  onActionButton(action: string): void {
    if (action === 'start-ride') this.onStartRide();
    if (action === 'finish-ride') this.onFinishRide();
    if (action === 'sos') console.log('SOS clicked');
    if (action === 'cancel') this.openCancelConfirmation();
  }

  private openCancelConfirmation(): void {
    this.showCancelConfirm = true;
  }

  confirmCancelRide(): void {
    this.showCancelConfirm = false;
    
    this.showCancelReasonPopup = true;
  }

  submitCancelReason(): void {
    if (!this.rideData || !this.cancelReason.trim()) return;

    const reason = this.cancelReason.trim();

    this.showCancelReasonPopup = false;
    this.clearTracking();

    this.rideService.cancelRide(this.rideData.rideId, reason).subscribe({
      next: () => {
        this.rideData!.status = 'CANCELLED';
        this.cancelReason = '';
        alert('Ride has been cancelled');
      },
      error: (err) => {
        console.error('Failed to cancel ride', err);
        alert('Failed to cancel ride');
      }
    });
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
      if (this.rideData) this.rideData.status = 'IN_PROGRESS';
      this.currentCoordinateIndex = 0;
      this.updateActionButtons();
      this.startRideSimulation();
      alert('Ride started');
    }
  }

  private onFinishRide(): void {
    this.clearTracking();
    if (this.rideData) this.rideData.status = 'COMPLETED';
    alert('Ride finished');
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

  private loadMockData(): void {
    this.rideData = {
      rideId: 9,
      status: 'ACCEPTED',
      currentLocation: { latitude: 45.26, longitude: 19.84 },
      estimatedArrivalMinutes: 8,
      route: {
        startLocation: { latitude: 45.2454, longitude: 19.8367 },
        startAddress: 'Gogoljeva 2, Novi Sad',
        endLocation: { latitude: 45.2462, longitude: 19.8524 },
        endAddress: 'Bulevar Oslobođenja 11, Novi Sad',
        waypoints: [
          {
            location: {
              latitude: 45.2458,
              longitude: 19.8445
            },
            address: 'Preradovićeva 72, Novi Sad',
            order: 1,
            isCompleted: false
          }
        ],
        totalDistanceKm: 2.5
      },
      passenger: {
        id: 1,
        firstName: 'Jane',
        lastName: 'Smith',
        phoneNumber: '+381 69 987 6543',
        profileImage: '../../../../public/profile.svg'
      },
      driver: {
        id: 2,
        firstName: 'Bob',
        lastName: 'Brown',
        phoneNumber: '+381 69 123 4567',
        profileImage: '../../../../public/profile.svg'
      },
      panicActivated: false
    };

    this.cardInfo = {
      label: 'Passenger',
      name: 'Jane Smith',
      phoneNumber: '+381 69 987 6543',
      profileImage: '../../../../public/profile.svg'
    };
  }
}
