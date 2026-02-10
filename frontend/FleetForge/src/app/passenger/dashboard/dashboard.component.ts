import { Component, OnInit, ChangeDetectorRef, WritableSignal, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MapComponent } from '../../shared/map/map';
import { VehicleService } from '../../shared/services/vehicle.service';
import { VehicleLocationDTO } from '../../shared/models/vehicle.model';
import { RideFavoriteRoutesDTO } from '../../shared/dtos/ride.dtos';
import { PassengerFavorite } from '../service/passenger-favorite/passenger-favorite';
import { RideService, ScheduledRideDto } from '../../shared/services/ride.service';

@Component({
  selector: 'app-passenger-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MapComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class PassengerDashboardComponent implements OnInit {

  vehicles: VehicleLocationDTO[] = [];
  
  scheduledRides: ScheduledRideDto[] = [];
  isLoadingScheduled = false;

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
    this.rideService.cancelRide(rideId).subscribe(() => {
      this.loadScheduledRides(); // refresh after cancel
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
