import { Component, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PassengerHistory } from '../service/passenger-history/passenger-history';
import { PassengerFavorite } from '../service/passenger-favorite/passenger-favorite';
import { RideFavoriteRoutesDTO } from '../../shared/dtos/ride.dtos';
import { RideRatingModalComponent, RatingFormData } from '../../shared/popups/ride-rating-modal/ride-rating-modal.component';
import { RideReviewService } from '../service/passenger-ride-review/ride-review.service';

interface Ride {
  id: number;
  pickupAddress: string;
  dropoffAddress: string;
  startDate: string;
  endDate: string | null;
  cancellationStatus: string;
  driverRating?: number; // 1-5 when rated
  vehicleRating?: number; // 1-5 when rated
  ratingComment?: string;
}

@Component({
  selector: 'app-passenger-history',
  standalone: true,
  imports: [CommonModule, FormsModule, RideRatingModalComponent],
  templateUrl: './passenger-history.component.html',
  styleUrls: ['./passenger-history.component.css'],
})
export class PassengerHistoryComponent {
  searchQuery: string = '';

  readonly starScale = [1, 2, 3, 4, 5];
  private readonly ratingWindowMs = 3 * 24 * 60 * 60 * 1000;

  private readonly favRideIds =signal<Set<number>>(new Set<number>());
  private readonly favRoutes: WritableSignal<RideFavoriteRoutesDTO[]> = signal<RideFavoriteRoutesDTO[]>([]);
  constructor(
    private passengerHistory: PassengerHistory,
    private passengerFavorite: PassengerFavorite,
    private rideReviewService: RideReviewService
  ) {}
  isRatingModalOpen = false;
  selectedRide: Ride | null = null;
  isRatingLoading = false;

	isFavoriteModalOpen = false;
	favoriteRide: Ride | null = null;
	favoriteRouteName = '';
  ratingForm: RatingFormData = {
    driverRating: 0,
    vehicleRating: 0,
    comment: '',
  };

  protected rides: WritableSignal<Ride[]> = signal<Ride[]>([]);

  ngOnInit(): void {
    this.passengerHistory.getPassengerRides().subscribe({
      next: (response) => {
        const mappedRides: Ride[] = response.content.map(r => ({
          id: r.rideId,
          pickupAddress: `${r.startLocation.latitude}, ${r.startLocation.longitude}`,
          dropoffAddress: `${r.endLocation.latitude}, ${r.endLocation.longitude}`,
          startDate: r.startTime,
          endDate: r.endTime,
          cancellationStatus: r.endTime ? 'Completed' : 'In progress',
          driverRating: r.driverRating ?? undefined,
          vehicleRating: r.vehicleRating ?? undefined,
        }));

        this.rides.set(mappedRides);
      },
      error: (err) => console.error('Failed to load passenger rides', err),
    });

    // favorites logic stays untouched
    this.passengerFavorite.getFavoriteRoutes().subscribe(routes => {
      const newFavs = new Set<number>();
      routes.forEach(route => newFavs.add(route.rideId));
      this.favRoutes.set(routes);
      this.favRideIds.set(newFavs);
    });
  }

  get displayedRides(): Ride[] {
    return this.rides();
  }


  getRatingState(ride: Ride): 'rated' | 'expired' | 'pending' {
    if (ride.driverRating !== undefined && ride.driverRating > 0) {
      return 'rated';
    }

    const rideTime = new Date(ride.startDate).getTime();
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

  submitRating(formData: RatingFormData): void {
    if (!this.selectedRide) {
      return;
    }

    this.isRatingLoading = true;

    const driverRating = Math.min(Math.max(formData.driverRating, 1), 5);
    const vehicleRating = Math.min(Math.max(formData.vehicleRating, 1), 5);
    console.log ("Submitting rating for ride", this.selectedRide.id, formData);
    this.rideReviewService
      .createReview(this.selectedRide.id, {
        driverRating,
        vehicleRating,
        comment: formData.comment.trim(),
      })
      .subscribe({
        next: (response) => {
          this.selectedRide!.driverRating = response.driverRating;
          this.selectedRide!.vehicleRating = response.vehicleRating;
          this.selectedRide!.ratingComment = response.comment;

          this.isRatingLoading = false;
          this.closeRatingModal();
        },
        error: (err) => {
          console.error('Failed to submit rating:', err);
          this.isRatingLoading = false;
        },
      });
  }

  onDetails(ride: Ride): void {
    console.log('Details for ride', ride);
  }

  isFavorite(ride: Ride): boolean {
    return this.favRideIds().has(ride.id);
  }   

  onFavoriteClick(ride: Ride): void {
    if (this.isFavorite(ride)) {
      this.removeFavorite(ride);
      return;
    }

    this.openFavoriteModal(ride);
  }

  openFavoriteModal(ride: Ride): void {
    this.favoriteRide = ride;
    this.favoriteRouteName = '';
    this.isFavoriteModalOpen = true;
  }

  closeFavoriteModal(): void {
    this.isFavoriteModalOpen = false;
    this.favoriteRide = null;
    this.favoriteRouteName = '';
  }

  saveFavoriteRoute(): void {
    if (!this.favoriteRide) {
      return;
    }

    const name = this.favoriteRouteName.trim();
    if (!name) {
      return;
    }

    const rideId = this.favoriteRide.id;
    this.passengerHistory.addFavoriteRoute(name, rideId).subscribe({
      next: () => {
        this.favRideIds.update((prev) => {
          const next = new Set(prev);
          next.add(rideId);
          return next;
        });

        this.passengerFavorite.getFavoriteRoutes().subscribe((routes) => {
          this.favRoutes.set(routes);
        });

        this.closeFavoriteModal();
      },
      error: (err) => {
        console.error('Failed to add favorite route', err);
      },
    });
  }

  private removeFavorite(ride: Ride): void {
    const rideId = ride.id;
    const favRouteId= this.favRoutes().find(route => route.rideId === rideId)?.id;
    if (favRouteId === undefined) {
      console.error('Favorite route ID not found for ride ID', rideId);
      return;
    }
    this.passengerHistory.deleteFavoriteRoute(favRouteId).subscribe({
      next: () => {
        this.favRideIds.update((prev) => {
          const next = new Set(prev);
          next.delete(rideId);
          return next;
        });
        this.favRoutes.update((prev) => prev.filter(route => route.rideId !== rideId));
      },
      error: (err:any) => {
        console.error('Failed to remove favorite route', err);
      },
    });
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
