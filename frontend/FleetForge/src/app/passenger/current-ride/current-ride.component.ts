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
    this.startLocationTracking();
  }

  ngOnDestroy(): void {
    if (this.locationUpdateInterval) {
      clearInterval(this.locationUpdateInterval);
    }
  }

  loadMockData(): void {
    this.rideData = {
      rideId: 1,
      status: 'IN_PROGRESS',
      currentLocation: {
        latitude: 45.2454,
        longitude: 19.8367
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
        profileImage: '../../../../public/profile.svg'
      },
      panicActivated: false
    };
  }

  onRouteCalculated(routeInfo: {distanceKm: number, estimatedMinutes: number}): void {
    this.calculatedDistance = routeInfo.distanceKm;
    this.calculatedTime = routeInfo.estimatedMinutes;
    
    if (this.rideData) {
      this.rideData.route.totalDistanceKm = routeInfo.distanceKm;
      this.rideData.estimatedArrivalMinutes = routeInfo.estimatedMinutes;
    }
  }

  onRouteCoordinatesReceived(coordinates: Array<{latitude: number, longitude: number}>): void {
    this.routeCoordinates = coordinates;
    this.currentCoordinateIndex = 0;
  }

  private startLocationTracking(): void {
    const waypointThreshold = 0.0005;
    
    this.locationUpdateInterval = setInterval(() => {
      if (!this.rideData || this.routeCoordinates.length === 0) {
        return;
      }

      const coordinatesPerUpdate = 5;
      const targetIndex = Math.min(
        this.currentCoordinateIndex + coordinatesPerUpdate,
        this.routeCoordinates.length - 1
      );

      if (this.currentCoordinateIndex < this.routeCoordinates.length - 1) {
        const coord = this.routeCoordinates[targetIndex];
        
        this.rideData.currentLocation.latitude = coord.latitude;
        this.rideData.currentLocation.longitude = coord.longitude;

        this.rideData.route.waypoints.forEach(waypoint => {
          if (!waypoint.isCompleted) {
            const latDiff = Math.abs(coord.latitude - waypoint.location.latitude);
            const lngDiff = Math.abs(coord.longitude - waypoint.location.longitude);
            
            if (latDiff < waypointThreshold && lngDiff < waypointThreshold) {
              waypoint.isCompleted = true;
              console.log(`Waypoint reached: ${waypoint.address}`);
            }
          }
        });

        if (this.mapComponent) {
          this.mapComponent.updateCurrentLocation(this.rideData.currentLocation);
        }

        this.currentCoordinateIndex = targetIndex;
      } else {
        if (this.locationUpdateInterval) {
          clearInterval(this.locationUpdateInterval);
        }
        console.log('Ride completed!');
      }
    }, 2000);
  }

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
    if (!this.wrongWayReport.trim()) {
      alert('Please enter a report before submitting.');
      return;
    }

    const reportData = {
      rideId: this.rideData?.rideId,
      report: this.wrongWayReport,
      currentLocation: this.rideData?.currentLocation
    };

    console.log('Wrong way report submitted:', reportData);
    alert('Report submitted successfully!');
    this.onCloseWrongWayModal();
  }

  onSOS(): void {
    // TODO: Implement SOS/panic functionality
    console.log('SOS clicked');
  }
}