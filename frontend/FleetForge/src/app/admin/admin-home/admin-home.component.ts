import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MapComponent } from '../../shared/map/map';
import { VehicleLocationDTO } from '../../shared/models/vehicle.model';
import { VehicleService } from '../../shared/services/vehicle.service';
import { Subscription, interval } from 'rxjs';
import { startWith, switchMap } from 'rxjs/operators';
import { AlarmService } from '../../shared/alarm/alarm.service';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [CommonModule, MapComponent],
  templateUrl: './admin-home.component.html',
  styleUrl: './admin-home.component.css',
  encapsulation: ViewEncapsulation.None
})
export class AdminHomeComponent implements OnInit {
  @ViewChild(MapComponent) mapComponent!: MapComponent;

  vehicles: VehicleLocationDTO[] = [];
  private pollingSubscription?: Subscription;

  constructor(
    private vehicleService: VehicleService,
    private alarmService: AlarmService
  ) {}

  ngOnInit(): void {
    this.pollingSubscription = interval(5000)
      .pipe(
        startWith(0),
        switchMap(() => this.vehicleService.getActiveVehicles())
      )
      .subscribe({
        next: (locations) => {
          this.vehicles = locations;
          this.checkPanicStatus(locations);
        },
        error: (err) => console.error('Error fetching vehicle locations:', err)
      });
  }

  private checkPanicStatus(locations: VehicleLocationDTO[]): void {
    const isAnyPanicActive = locations.some(v => v.panicActivated);

      if (isAnyPanicActive) {
        this.alarmService.start();
      } else {
        this.alarmService.stop();
      }
    }

  ngOnDestroy(): void {
    this.pollingSubscription?.unsubscribe();
  }

  handleMapClick(event: {address: string, lat: number, lng: number}): void {
    console.log('Admin clicked map at:', event.address);
  }
}
