import { Component, signal, WritableSignal, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PassengerHistory, PassengerRideDetailsDto } from '../service/passenger-history/passenger-history';
import { PassengerFavorite } from '../service/passenger-favorite/passenger-favorite';
import { RideFavoriteRoutesDTO } from '../../shared/dtos/ride.dtos';
import { RideRatingModalComponent, RatingFormData } from '../../shared/popups/ride-rating-modal/ride-rating-modal.component';
import { RideReviewService } from '../service/passenger-ride-review/ride-review.service';
import { MapComponent } from '../../shared/map/map';
import { Router } from '@angular/router';

type RideStatus = 'COMPLETED' | 'CANCELLED' | 'IN_PROGRESS';

interface Ride {
  id: number;
  pickupAddress: string;
  dropoffAddress: string;
  startDate: string;
  endDate: string | null;
  status: RideStatus;
  driverRating?: number;
  vehicleRating?: number;
  ratingComment?: string;
}

@Component({
  selector: 'app-passenger-history',
  standalone: true,
  imports: [CommonModule, FormsModule, RideRatingModalComponent, MapComponent],
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
    private rideReviewService: RideReviewService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}
  isRatingModalOpen = false;
  selectedRide: Ride | null = null;
  isRatingLoading = false;
  
  startDate: string = '';
  endDate: string = '';

	isFavoriteModalOpen = false;
	favoriteRide: Ride | null = null;
	favoriteRouteName = '';
  ratingForm: RatingFormData = {
    driverRating: 0,
    vehicleRating: 0,
    comment: '',
  };

  currentPage = signal(0);
  pageSize = signal(5);
  totalPages = signal(0);
  isLoading = signal(false);

  sortBy = signal<string>('startTime');
  sortDirection = signal<'asc' | 'desc'>('desc');

  expandedRideId: number | null = null;
  rideDetails = signal<Record<number, PassengerRideDetailsDto>>({});
  detailsLoading = signal<Set<number>>(new Set());


  protected rides: WritableSignal<Ride[]> = signal<Ride[]>([]);

  ngOnInit(): void {
    this.loadRides();

    this.passengerFavorite.getFavoriteRoutes().subscribe(routes => {
      const newFavs = new Set<number>();
      routes.forEach(route => newFavs.add(route.rideId));
      this.favRoutes.set(routes);
      this.favRideIds.set(newFavs);
    });
  }

  toggleDetails(ride: Ride): void {
    if (this.expandedRideId === ride.id) {
      this.expandedRideId = null;
      return;
    }

    this.expandedRideId = ride.id;

    if (this.rideDetails()[ride.id]) {
      return;
    }

    this.detailsLoading.update(s => new Set(s).add(ride.id));

    this.passengerHistory.getRideDetails(ride.id).subscribe({
      next: details => {
        this.rideDetails.update(prev => ({
          ...prev,
          [ride.id]: details,
        }));
        this.detailsLoading.update(s => {
          const next = new Set(s);
          next.delete(ride.id);
          return next;
        });
      },
      error: () => {
        this.detailsLoading.update(s => {
          const next = new Set(s);
          next.delete(ride.id);
          return next;
        });
      }
    });
  }

  rideAgain(ride: Ride): void {
    const details = this.getDetails(ride.id);
    if (!details) return;

    const favoriteRoute: RideFavoriteRoutesDTO = {
      id: -1,
      rideId: ride.id,
      startAddress: details.startAddress,
      endAddress: details.endAddress,
      name: `Ride from ${ride.pickupAddress}`,
      // Map history waypoints to the expected WayPointDTO format
      waypoints: details.wayPoints.map(wp => ({
        location: { latitude: wp.latitude, longitude: wp.longitude },
        address: "",
        orderIndex: details.wayPoints.indexOf(wp) + 1
      }))
    };

    this.router.navigate(['/passenger/passenger-home'], { 
      state: { favoriteRoute: favoriteRoute } 
    });
  }

  getStaticRoute(details: PassengerRideDetailsDto) {
    return {
      pickup: [details.startLocation.latitude, details.startLocation.longitude] as [number, number],
      dropoff: [details.endLocation.latitude, details.endLocation.longitude] as [number, number],
      waypoints: details.wayPoints.map(p => [p.latitude, p.longitude] as [number, number])
    };
  }


  isExpanded(ride: Ride): boolean {
    return this.expandedRideId === ride.id;
  }

  getDetails(rideId: number) {
    return this.rideDetails()[rideId];
  }

  loadRides(): void {
    this.isLoading.set(true);

    const startIso = this.startDate ? new Date(this.startDate).toISOString() : undefined;
    const endIso = this.endDate ? new Date(this.endDate).toISOString() : undefined;

    this.passengerHistory
      .getPassengerRides(
        this.currentPage(),
        this.pageSize(),
        this.sortBy(),
        this.sortDirection(),
        startIso,
        endIso
      )
      .subscribe({
        next: (response) => {
          const mappedRides: Ride[] = response.content.map(r => ({
            id: r.rideId,
            pickupAddress: r.startAddress,
            dropoffAddress: r.endAddress,
            startDate: r.startTime,
            endDate: r.endTime,
            status: r.status,
            driverRating: r.driverRating ?? undefined,
            vehicleRating: r.vehicleRating ?? undefined,
            averageReview: r.averageReview ?? undefined,
          }));

          this.rides.set(mappedRides);
          this.totalPages.set(response.totalPages);
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false),
      });
  }


  onSort(column: string): void {
    if (this.sortBy() === column) {
      this.sortDirection.set(
        this.sortDirection() === 'asc' ? 'desc' : 'asc'
      );
    } else {
      this.sortBy.set(column);
      this.sortDirection.set('desc');
    }

    this.currentPage.set(0);

    this.loadRides();
  }

  isSortedBy(column: string): boolean {
    return this.sortBy() === column;
  }


  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update(p => p + 1);
      this.loadRides();
    }
  }

  prevPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update(p => p - 1);
      this.loadRides();
    }
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
      this.loadRides();
    }
  }

  get displayedRides(): Ride[] {
    return this.rides();
  }


  getRatingState(ride: Ride): 'rated' | 'expired' | 'pending' | 'cancelled' {
    if (ride.status === 'CANCELLED') {
      return 'cancelled';
    }

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
          if (this.selectedRide) {
            this.selectedRide.driverRating = response.driverRating;
            this.selectedRide.vehicleRating = response.vehicleRating;
            this.selectedRide.ratingComment = response.comment;
          }

          this.isRatingLoading = false;
          this.isRatingModalOpen = false;
          this.selectedRide = null;
          this.ratingForm = { driverRating: 0, vehicleRating: 0, comment: '' };
          this.cdr.markForCheck();
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
}
