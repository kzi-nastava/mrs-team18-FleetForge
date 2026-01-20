import { Component, OnInit } from '@angular/core';
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
export class CurrentRideComponent implements OnInit {
  rideData: RideTrackingDTO | null = null;
  
  // Mock data for testing - will be replaced with actual API call
  ngOnInit(): void {
    this.loadMockData();
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
        startAddress: 'Djure Daničića 11',
        endLocation: {
          latitude: 45.2462,
          longitude: 19.8524
        },
        endAddress: 'Bulevar Oslobođenja 11',
        waypoints: [
          {
            location: {
              latitude: 45.2458,
              longitude: 19.8445
            },
            address: 'Preradovića 72',
            order: 1
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
