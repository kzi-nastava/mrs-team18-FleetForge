import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MapComponent } from '../shared/map/map';
import { VehicleService } from '../shared/services/vehicle.service';
import { VehicleLocationDTO } from '../shared/models/vehicle.model';

@Component({
  selector: 'app-home',
  imports: [RouterModule, MapComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  standalone: true
})
export class HomeComponent implements OnInit {
  vehicles: VehicleLocationDTO[] = [];

  constructor(private vehicleService: VehicleService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadVehicles();
  }

  loadVehicles(): void {
    this.vehicleService.getActiveVehicles().subscribe({
      next: (vehicles) => {
        this.vehicles = [...vehicles]; 
        this.cdr.detectChanges(); 
        console.log('Loaded vehicles:', this.vehicles);
        console.log('vehicles property after assignment:', this.vehicles);
      },
      error: (error) => {
        console.error('Error loading vehicles:', error);
      }
    });
  }
}
