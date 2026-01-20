import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MapComponent } from '../../shared/map/map';
import { RideTrackingDTO, DriverInfoDTO, RouteInfoDTO } from '../../shared/dtos/ride-tracking.dtos';

@Component({
  selector: 'app-current-ride',
  standalone: true,
  imports: [CommonModule, MapComponent],
  templateUrl: './current-ride.component.html',
  styleUrls: ['./current-ride.component.css']
})
export class CurrentRideComponent implements OnInit, OnDestroy {
  rideData: RideTrackingDTO | null = null;
  calculatedDistance: number = 0;
  calculatedTime: number = 0;
  private locationUpdateInterval: any;
  
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

  private startLocationTracking(): void {
    // Simulate current location updates (in real app, this would come from API)
    // For now, just keeping the initial position
    // You can add mock position changes here to test the tracking
  }

  getDriverRating(): number {
    // Will be replaced with actual rating from API
    return 3.0;
  }

  onWrongWay(): void {
    // TODO: Implement wrong way functionality
    console.log('Wrong way clicked');
  }

  onSOS(): void {
    // TODO: Implement SOS/panic functionality
    console.log('SOS clicked');
  }
}