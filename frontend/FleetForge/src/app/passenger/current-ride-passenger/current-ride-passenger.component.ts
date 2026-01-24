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
      panicActivated: false,
      passenger: {
        id: 2,
        firstName: 'Alice',
        lastName: 'Johnson',
        phoneNumber: '+381 69 765 4321',
        profileImage: '../../../../public/profile.svg'
      }
    };

    // Set card info from driver data
    if (this.rideData.driver) {
      this.cardInfo = {
        label: 'Driver',
        name: `${this.rideData.driver.firstName} ${this.rideData.driver.lastName}`,
        rating: this.getDriverRating(),
        phoneNumber: this.rideData.driver.phoneNumber,
        profileImage: this.rideData.driver.profileImage
      };
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

        if (this.currentRideComponent && this.currentRideComponent.mapComponent) {
          this.currentRideComponent.mapComponent.updateCurrentLocation(this.rideData.currentLocation);
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

  onActionButton(action: string): void {
    if (action === 'wrong-way') {
      this.onWrongWay();
    } else if (action === 'sos') {
      this.onSOS();
    }
  }

  private onWrongWay(): void {
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

  private onSOS(): void {
    // TODO: Implement SOS/panic functionality
    console.log('SOS clicked');
  }
}