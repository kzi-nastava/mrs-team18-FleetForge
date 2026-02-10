import { Component, signal, WritableSignal, ChangeDetectorRef, OnInit } from '@angular/core';
import { RideService, ScheduledRideDto } from '../../shared/services/ride.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConfirmationPopupComponent } from '../../shared/popups/confirmation-popup/confirmation-popup.component';
import { NotificationPopupComponent } from '../../shared/popups/popup-dialog/notification-popup.component';

interface ScheduledRide {
  id: number;
  pickupAddress: string;
  dropoffAddress: string;
  scheduledAt: string;
  estimatedCost: number;
  status: 'CANCELLED' | 'ACCEPTED';
}

@Component({
  selector: 'app-passenger-scheduled-rides',
  standalone: true,
  imports: [CommonModule, FormsModule, ConfirmationPopupComponent, NotificationPopupComponent],
  templateUrl: './passenger-scheduled-rides.component.html',
  styleUrls: ['./passenger-scheduled-rides.component.css'],
})
export class PassengerScheduledRidesComponent implements OnInit {
  searchQuery = '';
  showConfirmPopup = false;
  ridePendingCancel: ScheduledRide | null = null;

  notificationVisible = false;
  notificationTitle = '';
  notificationMessage = '';
  notificationSuccess = true;

  currentPage = signal(0);
  pageSize = signal(5);
  totalPages = signal(0);
  isLoading = signal(false);

  protected rides: WritableSignal<ScheduledRide[]> = signal([]);

  constructor(
    private rideService: RideService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRides();
  }

  get displayedRides(): ScheduledRide[] {
    const q = this.searchQuery.trim().toLowerCase();
    if (!q) return this.rides();

    return this.rides().filter(ride =>
      [ride.id, ride.pickupAddress, ride.dropoffAddress, new Date(ride.scheduledAt).toDateString(), ride.status]
        .join(' ')
        .toLowerCase()
        .includes(q)
    );
  }


  loadRides(): void {
    this.isLoading.set(true);

    this.rideService.getScheduledRides(this.currentPage(), this.pageSize()).subscribe({
      next: (response) => {
        const mapped: ScheduledRide[] = response.content.map(r => ({
          id: r.id,
          pickupAddress: r.pickup,
          dropoffAddress: r.dropoff,
          scheduledAt: r.scheduledTime,
          estimatedCost: r.estimatedCost,
          status: r.status,
        }));
        this.rides.set(mapped);
        this.totalPages.set(response.totalPages);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
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

  canCancel(ride: ScheduledRide): boolean {
    const rideTime = new Date(ride.scheduledAt).getTime();
    const now = Date.now();
    const diffMinutes = (rideTime - now) / 60000;
    return diffMinutes > 10 && ride.status === 'ACCEPTED';
  }

  requestCancel(ride: ScheduledRide): void {
    if (!this.canCancel(ride)) return;

    this.ridePendingCancel = ride;
    this.showConfirmPopup = true;
  }

  confirmCancel(): void {
    if (!this.ridePendingCancel) return;

    const rideId = this.ridePendingCancel.id;

    this.rideService.cancelRide(rideId).subscribe({
      next: (response) => {
        this.rides.update((rides) =>
          rides.map((r) =>
            r.id === rideId ? { ...r, status: 'CANCELLED' } : r
          )
        );

        this.notificationTitle = 'Ride Cancelled';
        this.notificationMessage = response.message;
        this.notificationSuccess = true;
        this.notificationVisible = true;

        this.closePopup();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.notificationTitle = 'Cancellation Failed';
        this.notificationMessage =
          err?.error?.message ?? 'Unable to cancel the scheduled ride.';
        this.notificationSuccess = false;
        this.notificationVisible = true;

        this.closePopup();
        this.cdr.detectChanges();
      },
    });
  }


  closePopup(): void {
    this.showConfirmPopup = false;
    this.ridePendingCancel = null;
  }

  onNotificationClosed(): void {
    this.notificationVisible = false;
  }
  onDetails(ride: ScheduledRide): void {
    console.log('Scheduled ride details', ride);
  }

  private futureDate(daysAhead: number): string {
    const d = new Date();
    d.setDate(d.getDate() + daysAhead);
    d.setHours(14, 0, 0, 0);
    return d.toISOString();
  }
}
