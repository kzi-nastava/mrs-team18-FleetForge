import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MapComponent } from '../../shared/map/map';
import { RideTrackingDTO } from '../../shared/dtos/ride-tracking.dtos';

@Component({
  selector: 'app-current-ride',
  standalone: true,
  imports: [CommonModule, MapComponent, FormsModule],
  templateUrl: './current-ride.component.html',
  styleUrls: ['./current-ride.component.css']
})
export class CurrentRideComponent implements OnInit, OnDestroy {
  @ViewChild(MapComponent) mapComponent!: MapComponent;
  
  rideData: RideTrackingDTO | null = null;
  calculatedDistance: number = 0;
  calculatedTime: number = 0;
  private locationUpdateInterval: any;
  private routeCoordinates: Array<{latitude: number, longitude: number}> = [];
  private currentCoordinateIndex: number = 0;
  showWrongWayModal: boolean = false;
  wrongWayReport: string = '';
  
  constructor() {}
  
  ngOnInit(): void {
    this.loadMockData();
    this.resolveSimulationFlow();
  }

  ngOnDestroy(): void {
    clearInterval(this.locationUpdateInterval);
  }

  /* ------------------------------------------------------------------
   *  STATE HELPERS 
   * ------------------------------------------------------------------ */

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

  /* ------------------------------------------------------------------
   *  SIMULATION ORCHESTRATION
   * ------------------------------------------------------------------ */

  private resolveSimulationFlow(): void {
    if (!this.rideData) return;

    if (this.isAccepted() && this.hasDriver()) {
      this.startDriverToPickupSimulation();
    }

    if (this.isInProgress()) {
      this.startRideSimulation();
    }
  }

  /* ------------------------------------------------------------------
   *  PHASE 1: DRIVER → PICKUP
   * ------------------------------------------------------------------ */
 

  private startDriverToPickupSimulation(): void {
    this.currentCoordinateIndex = 0;

    this.locationUpdateInterval = setInterval(() => {
      if (!this.rideData || this.routeCoordinates.length === 0) return;

      const targetIndex = Math.min(
        this.currentCoordinateIndex + 5, 
        this.routeCoordinates.length - 1
      );

      const coord = this.routeCoordinates[targetIndex];
      this.rideData.currentLocation.latitude = coord.latitude;
      this.rideData.currentLocation.longitude = coord.longitude;
      
      this.mapComponent?.updateCurrentLocation(this.rideData.currentLocation);
      this.currentCoordinateIndex = targetIndex;

      if (this.currentCoordinateIndex >= this.routeCoordinates.length - 1) {
        clearInterval(this.locationUpdateInterval);

        // 3 seconds for passengers to enter the vechile 
        setTimeout(() => {
          if (!this.rideData) return;

          this.rideData.status = 'IN_PROGRESS';
          this.currentCoordinateIndex = 0;
          this.startRideSimulation();
        }, 3000);
      }
    }, 1000); 
  }

  /* ------------------------------------------------------------------
   *  PHASE 2: RIDE IN PROGRESS
   * ------------------------------------------------------------------ */

  private startRideSimulation(): void {
    this.startLocationTracking();
  }

  private startLocationTracking(): void {
    const waypointThreshold = 0.0005;

    this.locationUpdateInterval = setInterval(() => {
      if (!this.rideData || this.routeCoordinates.length === 0) return;

      const targetIndex = Math.min(
        this.currentCoordinateIndex + 5,
        this.routeCoordinates.length - 1
      );

      if (this.currentCoordinateIndex < this.routeCoordinates.length - 1) {
        const coord = this.routeCoordinates[targetIndex];

        this.rideData.currentLocation.latitude = coord.latitude;
        this.rideData.currentLocation.longitude = coord.longitude;

        this.rideData.route.waypoints.forEach(wp => {
          if (!wp.isCompleted) {
            const latDiff = Math.abs(coord.latitude - wp.location.latitude);
            const lngDiff = Math.abs(coord.longitude - wp.location.longitude);

            if (latDiff < waypointThreshold && lngDiff < waypointThreshold) {
              wp.isCompleted = true;
            }
          }
        });

        this.mapComponent?.updateCurrentLocation(this.rideData.currentLocation);
        this.currentCoordinateIndex = targetIndex;
      } else {
        clearInterval(this.locationUpdateInterval);

        // 3 seconds for driver to click end button 
        setTimeout(() => {
          if (!this.rideData) return;
          this.rideData.status = 'COMPLETED';
        }, 3000);
      }
    }, 2000);
  }

  /* ------------------------------------------------------------------
   *  MAP CALLBACKS
   * ------------------------------------------------------------------ */

  onRouteCalculated(routeInfo: { distanceKm: number; estimatedMinutes: number }): void {
    this.calculatedDistance = routeInfo.distanceKm;
    this.calculatedTime = routeInfo.estimatedMinutes;

    if (this.rideData) {
      this.rideData.route.totalDistanceKm = routeInfo.distanceKm;
      this.rideData.estimatedArrivalMinutes = routeInfo.estimatedMinutes;
    }
  }

  onRouteCoordinatesReceived(
    coordinates: Array<{ latitude: number; longitude: number }>
  ): void {
    this.routeCoordinates = coordinates;
    this.currentCoordinateIndex = 0;
  }

  /* ------------------------------------------------------------------
   *  MOCK DATA
   * ------------------------------------------------------------------ */

  loadMockData(): void {
    this.rideData = {
      rideId: 1,
      status: 'ACCEPTED',
      currentLocation: {
        latitude: 45.26,
        longitude: 19.84
      },
      estimatedArrivalMinutes: 8,
      route: {
        startLocation: {
          latitude: 45.2454,
          longitude: 19.8367
        },
        startAddress: 'Gogoljeva 2, Novi Sad',
        endLocation: {
          latitude: 45.2462,
          longitude: 19.8524
        },
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
      driver: {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        phoneNumber: '+381 69 123 4567',
        profileImage: 'default.png',
      },
      panicActivated: false
    };
  }

  /* ------------------------------------------------------------------
   *  ACTIONS
   * ------------------------------------------------------------------ */
  getDriverRating(): number {
    // TODO: Fetch real driver rating from API
    return 3.0;
  }

  onWrongWay(): void {
    this.showWrongWayModal = true;
    this.wrongWayReport = '';
  }

  onCloseWrongWayModal(): void {
    this.showWrongWayModal = false;
    this.wrongWayReport = '';
  }

  onSubmitWrongWayReport(): void {
    if (!this.wrongWayReport.trim()) return;
    this.onCloseWrongWayModal();
  }

  onSOS(): void {
    // TODO: Implement SOS/panic functionality
    console.log('SOS clicked');
  }
}