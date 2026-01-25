import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MapComponent } from '../../shared/map/map';

interface Ride {
  name: string;
  id: string;
  pickupAddress: string;
  dropoffAddress: string;
  rideDate: string;
  totalCost: number;
  cancelledBy: 'driver' | 'passenger' | null;
  panicActivation: boolean;
  feedback: number; // 0-5 stars, 0 means waiting
  linkedPassengers: string[];
  pickupCoords: [number, number];
  dropoffCoords: [number, number];
  waypoints?: [number, number][];
}

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
  sortColumn: 'name' | 'pickupAddress' | 'dropoffAddress' | 'rideDate' | 'totalCost' = 'rideDate';
  sortDirection: 'asc' | 'desc' = 'desc';

  rides: Ride[] = [
    {
      name: 'Petar Petrovic',
      id: 'R123465798',
      pickupAddress: 'Kneza Milosa 3',
      dropoffAddress: 'Kajmakcalska 5',
      rideDate: '2025-10-24',
      totalCost: 1500.00,
      cancelledBy: null,
      panicActivation: false,
      feedback: 0,
      linkedPassengers: ['Jovan Jovanovic', 'Milica Markovic'],
      pickupCoords: [45.2562, 19.8443],
      dropoffCoords: [45.2478, 19.8572],
      waypoints: [[45.2534, 19.8501]]
    },
    {
      name: 'Ana Nikolic',
      id: 'R123465799',
      pickupAddress: 'Despota Stefana 4',
      dropoffAddress: 'Sekspiova 2',
      rideDate: '2025-10-24',
      totalCost: 2500.00,
      cancelledBy: 'passenger',
      panicActivation: false,
      feedback: 5,
      linkedPassengers: ['Igor Ilic'],
      pickupCoords: [45.2530, 19.8460],
      dropoffCoords: [45.2426, 19.8702],
      waypoints: [[45.2470, 19.8625], [45.2450, 19.8659]]
    },
    {
      name: 'Petar Petrovic',
      id: 'R123465800',
      pickupAddress: 'Kozacinskog 1',
      dropoffAddress: 'Staljinova 10',
      rideDate: '2025-10-18',
      totalCost: 450.00,
      cancelledBy: 'driver',
      panicActivation: false,
      feedback: 4,
      linkedPassengers: [],
      pickupCoords: [45.2479, 19.8365],
      dropoffCoords: [45.2403, 19.8309]
    },
    {
      name: 'Petar Petrovic',
      id: 'R123465801',
      pickupAddress: 'Mekinjeva 28',
      dropoffAddress: 'Mise Dimitrijevica 32',
      rideDate: '2025-10-08',
      totalCost: 552.00,
      cancelledBy: 'passenger',
      panicActivation: true,
      feedback: 4,
      linkedPassengers: ['Nikola Nikolic', 'Sara Savic'],
      pickupCoords: [45.2712, 19.8304],
      dropoffCoords: [45.2620, 19.8426],
      waypoints: [[45.2668, 19.8370]]
    }
  ];

  filteredRides: Ride[] = [];

  ngOnInit(): void {
    this.filterByDate();
  }

  getStarArray(rating: number): boolean[] {
    return Array(5).fill(false).map((_, index) => index < rating);
  }

  toggleDetails(ride: Ride): void {
    this.expandedRideId = this.expandedRideId === ride.id ? null : ride.id;
  }

  isExpanded(ride: Ride): boolean {
    return this.expandedRideId === ride.id;
  }

  filterByDate(): void {
    this.filteredRides = this.selectedDate
      ? this.rides.filter(ride => ride.rideDate === this.selectedDate)
      : [...this.rides];

    if (this.expandedRideId && !this.filteredRides.some(ride => ride.id === this.expandedRideId)) {
      this.expandedRideId = null;
    }

    this.applySorting();
  }

  sortBy(column: 'name' | 'pickupAddress' | 'dropoffAddress' | 'rideDate' | 'totalCost'): void {
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

  getCancellationLabel(ride: Ride): string {
    if (!ride.cancelledBy) {
      return 'Not cancelled';
    }

    return ride.cancelledBy === 'driver' ? 'Cancelled by driver' : 'Cancelled by passenger';
  }
}
