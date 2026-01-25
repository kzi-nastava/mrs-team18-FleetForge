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
  driverRating?: number; // 1-5 when rated
  vehicleRating?: number; // 1-5 when rated
  ratingComment?: string;
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

  readonly starScale = [1, 2, 3, 4, 5];
  private readonly ratingWindowMs = 3 * 24 * 60 * 60 * 1000;

  private readonly favoriteRideIds = new Set<string>();

  isRatingModalOpen = false;
  selectedRide: Ride | null = null;
  ratingForm = {
    driverRating: 0,
    vehicleRating: 0,
    comment: '',
  };

  hoverDriverRating = 0;
  hoverVehicleRating = 0;

  rides: Ride[] = [
    {
      name: 'Petar Petrovic',
      id: 'R123465798',
      pickupAddress: 'Kneza Milosa 3',
      dropoffAddress: 'Kajmakcalska 5',
      rideDate: this.buildRideDate(2),
      totalCost: 1500.0,
      cancellationStatus: 'Not cancelled',
      panicActivation: false,
      driverRating: 4,
      vehicleRating: 5,
      ratingComment: 'Great driving and clean car.',
    },
    {
      name: 'Petar Petrovic',
      id: 'R123465799',
      pickupAddress: 'Despota Stefana 4',
      dropoffAddress: 'Sekspiova 2',
      rideDate: this.buildRideDate(1),
      totalCost: 2500.0,
      cancellationStatus: 'By passenger',
      panicActivation: false,
    },
    {
      name: 'Petar Petrovic',
      id: 'R123465800',
      pickupAddress: 'Kozacinskog 1',
      dropoffAddress: 'Staljinova 10',
      rideDate: this.buildRideDate(6),
      totalCost: 450.0,
      cancellationStatus: 'By driver',
      panicActivation: false,
    },
    {
      name: 'Petar Petrovic',
      id: 'R123465801',
      pickupAddress: 'Mekinjeva 28',
      dropoffAddress: 'Mise Dimitrijevica 32',
      rideDate: this.buildRideDate(10),
      totalCost: 552.0,
      cancellationStatus: 'By passenger',
      panicActivation: true,
      driverRating: 5,
      vehicleRating: 4,
      ratingComment: 'Driver was courteous.',
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
        new Date(ride.rideDate).toDateString(),
        ride.cancellationStatus,
      ]
        .join(' ')
        .toLowerCase()
        .includes(q)
    );
  }

  getRatingState(ride: Ride): 'rated' | 'expired' | 'pending' {
    if (ride.driverRating !== undefined && ride.driverRating > 0) {
      return 'rated';
    }

    const rideTime = new Date(ride.rideDate).getTime();
    if (Number.isNaN(rideTime)) {
      return 'expired';
    }

    const now = Date.now();
    const deadline = rideTime + this.ratingWindowMs;
    return now > deadline ? 'expired' : 'pending';
  }

  openRating(ride: Ride): void {
    if (this.getRatingState(ride) !== 'pending') {
      return;
    }

    this.selectedRide = ride;
    this.ratingForm = {
      driverRating: ride.driverRating ?? 0,
      vehicleRating: ride.vehicleRating ?? 0,
      comment: ride.ratingComment ?? '',
    };
    this.isRatingModalOpen = true;
  }

  closeRatingModal(): void {
    this.isRatingModalOpen = false;
    this.selectedRide = null;
    this.ratingForm = { driverRating: 0, vehicleRating: 0, comment: '' };
  }

  setDriverRating(value: number): void {
    this.ratingForm.driverRating = value;
  }

  setVehicleRating(value: number): void {
    this.ratingForm.vehicleRating = value;
  }

  setDriverHover(value: number): void {
    this.hoverDriverRating = value;
  }

  clearDriverHover(): void {
    this.hoverDriverRating = 0;
  }

  setVehicleHover(value: number): void {
    this.hoverVehicleRating = value;
  }

  clearVehicleHover(): void {
    this.hoverVehicleRating = 0;
  }

  submitRating(): void {
    if (!this.selectedRide) {
      return;
    }

    const driverRating = Math.min(Math.max(this.ratingForm.driverRating, 1), 5);
    const vehicleRating = Math.min(Math.max(this.ratingForm.vehicleRating, 1), 5);

    this.selectedRide.driverRating = driverRating;
    this.selectedRide.vehicleRating = vehicleRating;
    this.selectedRide.ratingComment = this.ratingForm.comment.trim();

    this.closeRatingModal();
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

  getStarArray(rating: number): boolean[] {
    const safeRating = Math.max(0, Math.min(5, Math.floor(rating)));
    return Array(5)
      .fill(false)
      .map((_, index) => index < safeRating);
  }

  private buildRideDate(daysAgo: number): string {
    const date = new Date();
    date.setDate(date.getDate() - daysAgo);
    return date.toISOString();
  }
}
