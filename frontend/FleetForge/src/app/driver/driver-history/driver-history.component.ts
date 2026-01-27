import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MapComponent } from '../../shared/map/map';
import { CompletedRideDTO } from '../../shared/dtos/completed-ride.dto';
import { DriverHistoryService } from '../../driver/service/driver-history.service';

@Component({
  selector: 'app-driver-history',
  standalone: true,
  imports: [CommonModule, FormsModule, MapComponent],
  templateUrl: './driver-history.component.html',
  styleUrls: ['./driver-history.component.css']
})
export class DriverHistoryComponent implements OnInit {
  selectedDate: string = '';
  expandedRideId: string | null = null;
  sortColumn: 'passengerName' | 'pickupAddress' | 'dropoffAddress' | 'rideDate' | 'totalCost' = 'rideDate';
  sortDirection: 'asc' | 'desc' = 'desc';
  rides: CompletedRideDTO[] = [];
  filteredRides: CompletedRideDTO[] = [];

  constructor(private driverHistoryService: DriverHistoryService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.driverHistoryService.getDriverHistory().subscribe((rides) => {
      this.rides = rides;
      this.filterByDate();
      this.cdr.markForCheck();
    });
  }

  getStarArray(rating: number): boolean[] {
    return Array(5).fill(false).map((_, index) => index < rating);
  }

  toggleDetails(ride: CompletedRideDTO): void {
    this.expandedRideId = this.expandedRideId === ride.id.toString() ? null : ride.id.toString();
  }

  isExpanded(ride: CompletedRideDTO): boolean {
    return this.expandedRideId === ride.id.toString();
  }

  filterByDate(): void {
    this.filteredRides = this.selectedDate
      ? this.rides.filter(ride => ride.rideDate === this.selectedDate)
      : [...this.rides];

    if (this.expandedRideId && !this.filteredRides.some(ride => ride.id.toString() === this.expandedRideId)) {
      this.expandedRideId = null;
    }

    this.applySorting();
  }

  sortBy(column: 'passengerName' | 'pickupAddress' | 'dropoffAddress' | 'rideDate' | 'totalCost'): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.applySorting();
  }

  private applySorting(): void {
    this.filteredRides.sort((a, b) => {
      let aVal: any = a[this.sortColumn];
      let bVal: any = b[this.sortColumn];

      if (aVal < bVal) return this.sortDirection === 'asc' ? -1 : 1;
      if (aVal > bVal) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  }

  getCancellationLabel(ride: CompletedRideDTO): string {
    if (!ride.cancelledBy) {
      return 'Not cancelled';
    }
    if (ride.cancelledBy === 'driver') return 'Cancelled by driver';
    if (ride.cancelledBy === 'passenger') return 'Cancelled by passenger';
    return `Cancelled by ${ride.cancelledBy}`;
  }
}
