import { Component, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { DriverProfileChangesService } from '../service/driver-profile-changes/driver-profile-changes-service';


interface AdminRide {
  rideId: number;
  startTime: string;
  endTime: string;
  startAddress: string;
  destinationAddress: string;
  status: string;
  panicActivated: boolean;
}

@Component({
  selector: 'app-admin-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
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

  sortBy = signal<string>('startTime');
  sortDirection = signal<'asc' | 'desc'>('desc');

  protected rides: WritableSignal<AdminRide[]> = signal<AdminRide[]>([]);

  constructor(
    private router: Router,
    private driverService: DriverProfileChangesService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRides();
    
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
    this.isLoading.set(true);

    fetch('/api/admin/rides?username=' + this.searchUsername + 
          '&startDate=' + this.startDate + '&endDate=' + this.endDate)
      .then(res => res.json())
      .then(data => {
        this.rides.set(data.content);
        this.totalPages.set(data.totalPages);
        this.isLoading.set(false);
      })
      .catch(() => this.isLoading.set(false));
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

  viewDetails(ride: AdminRide) {
    console.log('View details for ride', ride.rideId);
    // Navigate to details page if needed
    // this.router.navigate(['/admin/ride', ride.rideId]);
  }

  get displayedRides(): AdminRide[] {
    return this.rides();
  }
}
