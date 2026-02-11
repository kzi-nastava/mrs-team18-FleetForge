import { Component, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MapComponent } from '../../shared/map/map';
import { ChangeDetectorRef } from '@angular/core';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { DriverProfileChangesService, AdminRide, AdminRideDetailsDto} from '../service/driver-profile-changes/driver-profile-changes-service';
import { NotificationPopupComponent } from '../../shared/popups/popup-dialog/notification-popup.component';

@Component({
  selector: 'app-admin-history',
  standalone: true,
  imports: [CommonModule, FormsModule, MapComponent, NotificationPopupComponent],
  templateUrl: './admin-history.component.html',
  styleUrls: ['./admin-history.component.css']
})
export class AdminHistoryComponent {
  searchUsername: string = '';
  startDate: string = '';
  endDate: string = '';

  usernameSuggestions: string[] = [];
  showSuggestions: boolean = false;
  private searchSubject = new Subject<string>();

  currentPage = signal(0);
  pageSize = signal(10);
  totalPages = signal(0);
  isLoading = signal(false);

  showPopup = signal(false);
  popupMessage = signal('');
  popupSuccess = signal(true);

  sortBy = signal<string>('startTime');
  sortDirection = signal<'asc' | 'desc'>('desc');
  
  expandedRideId: number | null = null;

  rideDetails = signal<Record<number, AdminRideDetailsDto>>({});
  detailsLoading = signal<Set<number>>(new Set());

  protected rides: WritableSignal<AdminRide[]> = signal<AdminRide[]>([]);

  constructor(
    private driverService: DriverProfileChangesService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.searchSubject.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(prefix => this.driverService.searchUsersByPrefix(prefix))
      ).subscribe(suggestions => {
        this.usernameSuggestions = suggestions;
        this.showSuggestions = suggestions.length > 0;

        this.cdr.detectChanges();
      });
  }

  getStarArray(rating: number): boolean[] {
    const safeRating = Math.max(0, Math.min(5, Math.floor(rating)));
    return Array(5)
      .fill(false)
      .map((_, index) => index < safeRating);
  }
  
  closePopup(): void {
    this.showPopup.set(false);
  }

  toggleDetails(ride: AdminRide): void {
    if (this.expandedRideId === ride.rideId) {
      this.expandedRideId = null;
      return;
    }

    this.expandedRideId = ride.rideId;

    if (this.rideDetails()[ride.rideId]) {
      return;
    }

    this.detailsLoading.update(s => new Set(s).add(ride.rideId));

    this.driverService.getRideDetails(ride.rideId).subscribe({
      next: details => {
        this.rideDetails.update(prev => ({
          ...prev,
          [ride.rideId]: details,
        }));
        this.detailsLoading.update(s => {
          const next = new Set(s);
          next.delete(ride.rideId);
          return next;
        });
      },
      error: () => {
        this.detailsLoading.update(s => {
          const next = new Set(s);
          next.delete(ride.rideId);
          return next;
        });
      }
    });
  }

  isExpanded(ride: AdminRide): boolean {
    return this.expandedRideId === ride.rideId;
  }

  getDetails(rideId: number) {
    return this.rideDetails()[rideId];
  }

  getStaticRoute(details: AdminRideDetailsDto) {
    return {
      pickup: [details.startLocation.latitude, details.startLocation.longitude] as [number, number],
      dropoff: [details.endLocation.latitude, details.endLocation.longitude] as [number, number],
      waypoints: details.wayPoints.map(p => [p.latitude, p.longitude] as [number, number])
    };
  }

  onUsernameInputChange(): void {
    if (this.searchUsername.trim() === '') {
      this.showSuggestions = false;
      this.usernameSuggestions = [];
      return;
    }
        
    this.showSuggestions = true;

    this.searchSubject.next(this.searchUsername);
  }

  selectSuggestion(suggestion: string): void {
    this.searchUsername = suggestion;
    this.showSuggestions = false;
    this.loadRides();
  }

  hideSuggestionsWithDelay(): void {
    setTimeout(() => {
      this.showSuggestions = false;
    }, 200);
  }

  loadRides(): void {
    if (!this.searchUsername.trim()) {
      this.popupMessage.set('Username cannot be empty');
      this.popupSuccess.set(false);
      this.showPopup.set(true);
      return;
    }

    if (this.startDate && this.endDate && new Date(this.startDate) > new Date(this.endDate)) {
      this.popupMessage.set('Start date cannot be later than end date');
      this.popupSuccess.set(false);
      this.showPopup.set(true);
      return;
    }

    this.showSuggestions = false;
    this.isLoading.set(true);

    const startIso = this.startDate ? new Date(this.startDate).toISOString() : undefined;
    const endIso = this.endDate ? new Date(this.endDate).toISOString() : undefined;

    this.driverService
      .getRides(
        this.searchUsername,
        startIso,
        endIso,
        this.currentPage(),
        this.pageSize(),
        this.sortBy(),
        this.sortDirection()
      )
      .subscribe({
        next: (data: any) => {
          this.rides.set(data.content);
          this.totalPages.set(data.totalPages);
          this.isLoading.set(false);

        },
        error: (err: any) => {
          this.isLoading.set(false);

          let message = 'An unexpected error occurred';
          if (typeof err.error === 'string') {
            message = err.error; 
          } else if (err?.error?.message) {
            message = err.error.message;
          }

          this.popupMessage.set(message);
          this.popupSuccess.set(false);
          this.showPopup.set(true);
        },
      });
  }




  onSort(column: string): void {
    if (this.sortBy() === column) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
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

  get displayedRides(): AdminRide[] {
    return this.rides();
  }
}
