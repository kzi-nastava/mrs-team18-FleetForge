import { Component, OnInit, ChangeDetectorRef, WritableSignal, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MapComponent } from '../../shared/map/map';
import { VehicleService } from '../../shared/services/vehicle.service';
import { VehicleLocationDTO } from '../../shared/models/vehicle.model';
import { RideFavoriteRoutesDTO } from '../../shared/dtos/ride.dtos';
import { PassengerFavorite } from '../service/passenger-favorite/passenger-favorite';
import { RideService, ScheduledRideDto } from '../../shared/services/ride.service';
import { NotificationPopupComponent } from '../../shared/popups/popup-dialog/notification-popup.component';
import { ConfirmationPopupComponent } from '../../shared/popups/confirmation-popup/confirmation-popup.component';

@Component({
  selector: 'app-passenger-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MapComponent,
    NotificationPopupComponent,
    ConfirmationPopupComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class PassengerDashboardComponent implements OnInit {

  vehicles: VehicleLocationDTO[] = [];
  
  scheduledRides: ScheduledRideDto[] = [];
  isLoadingScheduled = false;

  showPopup = false;
  popupSuccess = false;
  popupMessage = '';
  
  showConfirmPopup = false;
  rideToCancelId: number | null = null;

  favoriteRoutes: WritableSignal<RideFavoriteRoutesDTO[]> = signal<RideFavoriteRoutesDTO[]>([]);

  constructor(
    private vehicleService: VehicleService,
    private rideService: RideService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private passengerFavorite: PassengerFavorite
  ) {}

  ngOnInit(): void {
    this.loadVehicles();
    this.loadScheduledRides();

    this.passengerFavorite.getFavoriteRoutes().subscribe(routes => {
      this.favoriteRoutes.set(routes);
    });
  }

  closePopup(): void {
    this.showPopup = false;
  }

  isCancelDisabled(ride: ScheduledRideDto): boolean {
    const now = new Date().getTime();
    const scheduledTime = new Date(ride.scheduledTime).getTime();

    const diffInMinutes = (scheduledTime - now) / (1000 * 60);

    // Disable if ride starts in 10 minutes or less
    return diffInMinutes <= 10;
  }

  openCancelConfirmation(rideId: number): void {
  this.rideToCancelId = rideId;
  this.showConfirmPopup = true;
}

onCancelConfirmed(): void {
  if (!this.rideToCancelId) return;

  this.rideService.cancelRide(this.rideToCancelId).subscribe({
      next: (res) => {
        this.popupSuccess = true;
        this.popupMessage = res.message || 'Ride cancelled successfully.';
        this.showPopup = true;

        this.loadScheduledRides();
      },
      error: () => {
        this.popupSuccess = false;
        this.popupMessage = 'Failed to cancel ride.';
        this.showPopup = true;
      },
      complete: () => {
        this.resetConfirmation();
      }
    });
  }

  onCancelDismissed(): void {
    this.resetConfirmation();
  }

  private resetConfirmation(): void {
    this.showConfirmPopup = false;
    this.rideToCancelId = null;
  }


  private loadScheduledRides(): void {
    this.isLoadingScheduled = true;

    // request only 2 newest scheduled rides
    this.rideService.getScheduledRides(0, 2).subscribe({
      next: (res) => {
        this.scheduledRides = res.content;
        this.isLoadingScheduled = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load scheduled rides', err);
        this.isLoadingScheduled = false;
      }
    });
  }

  cancelRide(rideId: number): void {
    this.rideService.cancelRide(rideId).subscribe({
      next: (res) => {
        this.popupSuccess = true;
        this.popupMessage = res.message || 'Ride cancelled successfully.';
        this.showPopup = true;

        this.loadScheduledRides();
      },
      error: () => {
        this.popupSuccess = false;
        this.popupMessage = 'Failed to cancel ride.';
        this.showPopup = true;
      }
    });
  }


  orderRide(route: RideFavoriteRoutesDTO): void {
    this.router.navigate(['/passenger/passenger-home'], {
      state: { favoriteRoute: route }
    });
  }

  private loadVehicles(): void {
    this.vehicleService.getActiveVehicles().subscribe({
      next: (vehicles) => {
        this.vehicles = vehicles;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load vehicles', err);
      }
    });
  }

}
