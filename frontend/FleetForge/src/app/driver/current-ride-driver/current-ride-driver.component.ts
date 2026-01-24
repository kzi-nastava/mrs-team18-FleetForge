import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CurrentRideComponent, ActionButton, CardInfo } from '../../shared/current-ride/current-ride.component';
import { RideTrackingDTO } from '../../shared/dtos/ride-tracking.dtos';

@Component({
  selector: 'app-current-ride-driver',
  standalone: true,
  imports: [CommonModule, CurrentRideComponent, FormsModule],
  templateUrl: './current-ride-driver.component.html',
  styleUrls: ['./current-ride-driver.component.css']
})
export class CurrentRideDriverComponent implements OnInit, OnDestroy {
  @ViewChild(CurrentRideComponent) currentRideComponent!: CurrentRideComponent;
  
  rideData: RideTrackingDTO | null = null;
  cardInfo: CardInfo | null = null;
  actionButtons: ActionButton[] = [];
  
  private locationUpdateInterval: any;
  private routeCoordinates: Array<{latitude: number, longitude: number}> = [];
  private currentCoordinateIndex: number = 0;
  private rideStarted: boolean = false;

  
  constructor() {}
  
  ngOnInit(): void {
    this.loadMockData();
    this.updateActionButtons();
  }

  ngOnDestroy(): void {
    if (this.locationUpdateInterval) {
      clearInterval(this.locationUpdateInterval);
    }
  }

  loadMockData(): void {
    this.rideData = {
      rideId: 1,
      status: 'ACCEPTED',
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

    // Set card info from passenger data
    if (this.rideData.passenger) {
      this.cardInfo = {
        label: 'Passenger',
        name: `${this.rideData.passenger.firstName} ${this.rideData.passenger.lastName}`,
        phoneNumber: this.rideData.passenger.phoneNumber,
        profileImage: this.rideData.passenger.profileImage
      };
    }
  }

  onRouteCoordinatesReceived(coordinates: Array<{latitude: number, longitude: number}>): void {
    this.routeCoordinates = coordinates;
    this.currentCoordinateIndex = 0;
  }

  private updateActionButtons(): void {
    if (!this.rideStarted) {
      this.actionButtons = [
        { label: 'Start Ride', color: 'success', action: 'start-ride' },
        { label: 'SOS', color: 'warn', action: 'sos' }
      ];
    } else {
      this.actionButtons = [
        { label: 'Finish Ride', color: 'primary', action: 'finish-ride' },
        { label: 'SOS', color: 'warn', action: 'sos' }
      ];
    }
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
        console.log('Destination reached!');
      }
    }, 2000);
  }

  onActionButton(action: string): void {
    if (action === 'start-ride') {
      this.onStartRide();
    } else if (action === 'finish-ride') {
      this.onFinishRide();
    } else if (action === 'sos') {
      this.onSOS();
    }
  }

  private onStartRide(): void {
    if (!this.rideStarted && this.routeCoordinates.length > 0) {
      this.rideStarted = true;
      if (this.rideData) {
        this.rideData.status = 'IN_PROGRESS';
      }
      this.updateActionButtons();
      this.startLocationTracking();
      alert('Ride started - passengers picked up');
    }
  }

  private onFinishRide(): void {
    if (this.rideStarted) {
      if (this.locationUpdateInterval) {
        clearInterval(this.locationUpdateInterval);
      }
      if (this.rideData) {
        this.rideData.status = 'COMPLETED';
      }
      console.log('Ride finished - destination reached');
      alert('Ride completed successfully!');
    }
  }

  private onSOS(): void {
    // TODO: Implement SOS/panic functionality
    console.log('SOS clicked');
  }

}
