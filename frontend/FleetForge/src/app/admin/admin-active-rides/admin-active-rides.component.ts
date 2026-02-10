import { Component, OnInit, OnDestroy, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminActiveRidesService } from '../service/active-rides/admin-active-rides.service';
import { ActiveRideDTO, ActiveRideDetailsDTO } from '../../shared/dtos/active-ride.dtos';
import { MapComponent } from '../../shared/map/map';
import { interval, Subscription, of } from 'rxjs';
import { catchError, startWith, switchMap } from 'rxjs/operators';
import { RideTrackingDTO } from '../../shared/dtos/ride-tracking.dtos';

@Component({
  selector: 'app-active-rides',
  standalone: true,
  imports: [CommonModule, MapComponent],
  templateUrl: './admin-active-rides.component.html',
  styleUrls: ['./admin-active-rides.component.css']
})
export class AdminActiveRidesComponent implements OnInit, OnDestroy {
  @ViewChild(MapComponent) mapComponent!: MapComponent;

  activeRides: ActiveRideDTO[] = [];
  expandedRideId: number | null = null;
  rideDetails: ActiveRideDetailsDTO | null = null;
  
  private detailsPollingSubscription?: Subscription;
  
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private adminActiveRidesService: AdminActiveRidesService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadActiveRides();
  }

  ngOnDestroy(): void {
    this.stopDetailsPolling();
  }

  loadActiveRides(): void {
    this.isLoading = true;
    this.errorMessage = null;
    
    this.adminActiveRidesService.getAllActiveRides().subscribe({
      next: (rides) => {
        this.activeRides = rides;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading active rides:', error);
        this.errorMessage = 'Failed to load active rides. Please try again.';
        this.isLoading = false;
      }
    });
  }

  toggleDetails(rideId: number): void {
    if (this.expandedRideId === rideId) {
      this.collapseDetails();
    } else {
      this.expandDetails(rideId);
    }
  }

  private expandDetails(rideId: number): void {
    this.stopDetailsPolling();
    this.expandedRideId = rideId;
    this.startDetailsPolling(rideId);
  }

  private collapseDetails(): void {
    this.stopDetailsPolling();
    this.expandedRideId = null;
    this.rideDetails = null;
  }

  private startDetailsPolling(rideId: number): void {
    this.detailsPollingSubscription = interval(2000)
      .pipe(
        startWith(0),
        switchMap(() => this.adminActiveRidesService.getActiveRideDetails(rideId)
          .pipe(
            catchError(error => {
              console.error('Error fetching ride details:', error);
              if (error?.status === 404) {
                this.collapseDetails();
                this.loadActiveRides();
              }
              return of(null);
            })
          )
        )
      )
      .subscribe((details) => {
        if (details) {
          this.rideDetails = details;
          this.cdr.detectChanges();
          
          if (this.mapComponent && details.currentLocation) {
            this.mapComponent.updateCurrentLocation(details.currentLocation);
          }
        }
      });
  }

  private stopDetailsPolling(): void {
    if (this.detailsPollingSubscription) {
      this.detailsPollingSubscription.unsubscribe();
      this.detailsPollingSubscription = undefined;
    }
  }

  isExpanded(rideId: number): boolean {
    return this.expandedRideId === rideId;
  }

  getRideTrackingData(): RideTrackingDTO | null {
    if (!this.rideDetails) return null;

    return {
      rideId: this.rideDetails.rideId,
      status: 'IN_PROGRESS',
      currentLocation: this.rideDetails.currentLocation,
      route: {
        startLocation: this.rideDetails.startLocation,
        startAddress: this.rideDetails.startAddress,
        endLocation: this.rideDetails.endLocation,
        endAddress: this.rideDetails.endAddress,
        waypoints: [],
        totalDistanceKm: 0
      },
      estimatedArrivalMinutes: 0,
      driver: {
          id: 0,
        firstName: this.rideDetails.driverFirstName,
        lastName: this.rideDetails.driverLastName,
        phoneNumber: this.rideDetails.driverPhoneNumber,
        profileImage: this.rideDetails.driverProfileImage
      },
      panicActivated: this.rideDetails.panicActivated,
      passenger: {
        id: 0,
        firstName: '',
        lastName: '',
        phoneNumber: '',
        profileImage: ''
      }
    };
  }


  formatDateTime(dateTime: string): string {
    const date = new Date(dateTime);
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false });
  }

  refresh(): void {
    this.loadActiveRides();
  }
}
