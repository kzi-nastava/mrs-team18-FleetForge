import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MapComponent } from '../../shared/map/map';
import { VehicleService } from '../../shared/services/vehicle.service';
import { VehicleLocationDTO } from '../../shared/models/vehicle.model';

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

  favoriteRoutes = [
    {
      name: 'Home → Work',
      vehicleType: 'Standard Vehicle'
    },
    {
      name: 'Gym Route',
      vehicleType: 'Luxury Vehicle'
    }
  ];

  constructor(
    private vehicleService: VehicleService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadVehicles();
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
