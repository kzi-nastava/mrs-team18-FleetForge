import { Component, OnInit, ChangeDetectorRef, WritableSignal, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MapComponent } from '../../shared/map/map';
import { VehicleService } from '../../shared/services/vehicle.service';
import { VehicleLocationDTO } from '../../shared/models/vehicle.model';
import { RideFavoriteRoutesDTO } from '../../shared/dtos/ride.dtos';
import { PassengerFavorite } from '../service/passenger-favorite/passenger-favorite';

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

  scheduledRides = [
    {
      date: '24 JAN, 18:30',
      from: 'Bulevar oslobođenja 12',
      to: 'Fruškogorska 4'
    },
    {
      date: '26 JAN, 09:00',
      from: 'Cara Dušana 45',
      to: 'Airport'
    }
  ];

  favoriteRoutes: WritableSignal<RideFavoriteRoutesDTO[]> = signal<RideFavoriteRoutesDTO[]>([]);

  constructor(
    private vehicleService: VehicleService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private passengerFavorite: PassengerFavorite
  ) {}

  ngOnInit(): void {
    this.loadVehicles();
    this.passengerFavorite.getFavoriteRoutes().subscribe(routes => {
      this.favoriteRoutes.set(routes);
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
  orderRide(route: RideFavoriteRoutesDTO): void {
    this.router.navigate(['/passenger/passenger-home'], { state: { favoriteRoute: route } });
  }

}
