import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CurrentRideComponent, ActionButton, CardInfo } from '../../shared/current-ride/current-ride.component';
import { RideTrackingDTO } from '../../shared/dtos/ride-tracking.dtos';

@Component({
  selector: 'app-current-ride-passenger',
  standalone: true,
  imports: [CommonModule, CurrentRideComponent, FormsModule],
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
  
  private locationUpdateInterval: any;
  private routeCoordinates: Array<{latitude: number, longitude: number}> = [];
  private currentCoordinateIndex: number = 0;
  
  showWrongWayModal: boolean = false;
  wrongWayReport: string = '';
  
  constructor() {}
  
  ngOnInit(): void {
    this.loadMockData();
  }

  ngOnDestroy(): void {
    this.clearTracking();
  }

  private clearTracking() {
    if (this.locationUpdateInterval) {
      clearInterval(this.locationUpdateInterval);
    }
  }

  get activeActionButtons(): ActionButton[] {
    //todo add rate buttons after ride is completed
    if (this.isAccepted()) {
      return [];
    }
    return this.actionButtons;
  }

  onRouteCoordinatesReceived(coordinates: Array<{latitude: number, longitude: number}>): void {
    this.routeCoordinates = coordinates;
    this.currentCoordinateIndex = 0;
    this.resolveSimulationFlow();
  }

  private resolveSimulationFlow(): void {
    this.clearTracking();
    if (!this.rideData) return;

    if (this.rideData.status === 'ACCEPTED') {
      this.startDriverToPickupSimulation();
    } else if (this.rideData.status === 'IN_PROGRESS') {
      this.startRideSimulation();
    }
  }

  private startDriverToPickupSimulation(): void {
    this.locationUpdateInterval = setInterval(() => {
      if (!this.rideData || this.routeCoordinates.length === 0) return;

      const targetIndex = Math.min(this.currentCoordinateIndex + 5, this.routeCoordinates.length - 1);
      const coord = this.routeCoordinates[targetIndex];
      
      this.updateLocationOnMap(coord);
      this.currentCoordinateIndex = targetIndex;

      if (this.currentCoordinateIndex >= this.routeCoordinates.length - 1) {
        this.clearTracking();
        // Transition to IN_PROGRESS after 3 seconds
        setTimeout(() => {
          if (this.rideData) {
            this.rideData.status = 'IN_PROGRESS';
            this.currentCoordinateIndex = 0;
            this.startRideSimulation();
          }
        }, 3000);
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
          if (latDiff < waypointThreshold && lngDiff < waypointThreshold) wp.isCompleted = true;
        }
      });

      this.updateLocationOnMap(coord);
      this.currentCoordinateIndex = targetIndex;

      if (this.currentCoordinateIndex >= this.routeCoordinates.length - 1) {
        this.clearTracking();
        setTimeout(() => { if (this.rideData) this.rideData.status = 'COMPLETED'; }, 3000);
      }
    }, 2000);
  }

  private updateLocationOnMap(coord: {latitude: number, longitude: number}) {
    if (this.rideData) {
      this.rideData.currentLocation.latitude = coord.latitude;
      this.rideData.currentLocation.longitude = coord.longitude;
      this.currentRideComponent?.mapComponent?.updateCurrentLocation(this.rideData.currentLocation);
    }
  }

  loadMockData(): void {
  const mockRide: RideTrackingDTO = {
    rideId: 1,
    status: 'ACCEPTED',
    currentLocation: { latitude: 45.26, longitude: 19.84 },
    estimatedArrivalMinutes: 8,
    route: {
      startLocation: { latitude: 45.2454, longitude: 19.8367 },
      startAddress: 'Gogoljeva 2, Novi Sad',
      endLocation: { latitude: 45.2462, longitude: 19.8524 },
      endAddress: 'Bulevar Oslobođenja 11, Novi Sad',
      waypoints: [{
        location: { latitude: 45.2458, longitude: 19.8445 },
        address: 'Preradovićeva 72, Novi Sad',
        order: 1,
        isCompleted: false
      }],
      totalDistanceKm: 2.5
    },
    driver: {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      phoneNumber: '+381 69 123 4567',
      profileImage: '../../../../public/profile.svg'
    },
    passenger: {
      id: 2,
      firstName: 'Alice',
      lastName: 'Johnson',
      phoneNumber: '+381 69 765 4321',
      profileImage: '../../../../public/profile.svg'
    },
    panicActivated: false
  };

  this.rideData = mockRide;

  if (this.rideData && this.rideData.driver) {
    const driver = this.rideData.driver; 
    
    this.cardInfo = {
      label: 'Driver',
      name: `${driver.firstName} ${driver.lastName}`,
      rating: 4.8,
      phoneNumber: driver.phoneNumber,
      profileImage: driver.profileImage
    };
  }
}

  onActionButton(action: string): void {
    if (action === 'wrong-way') this.showWrongWayModal = true;
    if (action === 'sos') console.log('SOS triggered');
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
}