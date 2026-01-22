import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Ride {
  name: string;
  id: string;
  pickupAddress: string;
  dropoffAddress: string;
  rideDate: string;
  totalCost: number;
  cancellationStatus: string;
  panicActivation: boolean;
}

@Component({
  selector: 'app-passenger-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './passenger-history.component.html',
  styleUrls: ['./passenger-history.component.css'],
})
export class PassengerHistoryComponent {
  searchQuery: string = '';

  private readonly favoriteRideIds = new Set<string>();

  rides: Ride[] = [
    {
      name: 'Petar Petrovic',
      id: 'R123465798',
      pickupAddress: 'Kneza Milosa 3',
      dropoffAddress: 'Kajmakcalska 5',
      rideDate: '24 Oct, 2025',
      totalCost: 1500.0,
      cancellationStatus: 'Not cancelled',
      panicActivation: false,
    },
    {
      name: 'Petar Petrovic',
      id: 'R123465799',
      pickupAddress: 'Despota Stefana 4',
      dropoffAddress: 'Sekspiova 2',
      rideDate: '24 Oct, 2025',
      totalCost: 2500.0,
      cancellationStatus: 'By passenger',
      panicActivation: false,
    },
    {
      name: 'Petar Petrovic',
      id: 'R123465800',
      pickupAddress: 'Kozacinskog 1',
      dropoffAddress: 'Staljinova 10',
      rideDate: '18 Oct, 2025',
      totalCost: 450.0,
      cancellationStatus: 'By driver',
      panicActivation: false,
    },
    {
      name: 'Petar Petrovic',
      id: 'R123465801',
      pickupAddress: 'Mekinjeva 28',
      dropoffAddress: 'Mise Dimitrijevica 32',
      rideDate: '8 Oct, 2025',
      totalCost: 552.0,
      cancellationStatus: 'By passenger',
      panicActivation: true,
    },
  ];

  get displayedRides(): Ride[] {
    const q = this.searchQuery.trim().toLowerCase();
    if (!q) return this.rides;

    return this.rides.filter((ride) =>
      [
        ride.name,
        ride.id,
        ride.pickupAddress,
        ride.dropoffAddress,
        ride.rideDate,
        ride.cancellationStatus,
      ]
        .join(' ')
        .toLowerCase()
        .includes(q)
    );
  }

  onRate(ride: Ride): void {
    console.log('Rate ride', ride);
  }

  onDetails(ride: Ride): void {
    console.log('Details for ride', ride);
  }

  isFavorite(ride: Ride): boolean {
    return this.favoriteRideIds.has(ride.id);
  }

  toggleFavorite(ride: Ride): void {
    if (this.favoriteRideIds.has(ride.id)) {
      this.favoriteRideIds.delete(ride.id);
      return;
    }

    this.favoriteRideIds.add(ride.id);
  }
}
