import { Component, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RideService } from '../../shared/services/ride.service';
import { ConfirmationPopupComponent } from '../../shared/popups/confirmation-popup/confirmation-popup.component';

interface ScheduledRide {
  id: number;
  pickupAddress: string;
  dropoffAddress: string;
  scheduledAt: string;
  estimatedCost: number;
  status: 'SCHEDULED' | 'CANCELLED';
}

@Component({
  selector: 'app-passenger-scheduled-rides',
  standalone: true,
  imports: [CommonModule, FormsModule, ConfirmationPopupComponent],
  templateUrl: './passenger-scheduled-rides.component.html',
  styleUrls: ['./passenger-scheduled-rides.component.css'],
})
export class PassengerScheduledRidesComponent {
  searchQuery = '';
  
  showConfirmPopup = false;
  ridePendingCancel: ScheduledRide | null = null;

  constructor(private rideService: RideService) {}

  protected rides: WritableSignal<ScheduledRide[]> = signal([
    {
      id: 7,
      pickupAddress: 'Bulevar oslobođenja 12, Novi Sad',
      dropoffAddress: 'Liman 4',
      scheduledAt: new Date(Date.now() + 5 * 60 * 1000).toISOString(),
      estimatedCost: 920,
      status: 'SCHEDULED',
    },
    {
      id: 8,
      pickupAddress: 'Cara Dušana 55, Novi Sad',
      dropoffAddress: 'Petrovaradin',
      scheduledAt: this.futureDate(1),
      estimatedCost: 1100,
      status: 'SCHEDULED',
    },
    {
      id: 9,
      pickupAddress: 'Futoška 18, Novi Sad',
      dropoffAddress: 'Trg slobode',
      scheduledAt: this.futureDate(5),
      estimatedCost: 760,
      status: 'SCHEDULED',
    },
  ]);

  get displayedRides(): ScheduledRide[] {
    const q = this.searchQuery.trim().toLowerCase();
    if (!q) return this.rides();

    return this.rides().filter((ride) =>
      [
        ride.id,
        ride.pickupAddress,
        ride.dropoffAddress,
        new Date(ride.scheduledAt).toDateString(),
        ride.status,
      ]
        .join(' ')
        .toLowerCase()
        .includes(q)
    );
  }

  canCancel(ride: ScheduledRide): boolean {
    const rideTime = new Date(ride.scheduledAt).getTime();
    const now = Date.now();
    const diffMinutes = (rideTime - now) / 60000;
    return diffMinutes > 10 && ride.status === 'SCHEDULED';
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
      next: () => {
        this.rides.update((rides) =>
          rides.map((r) =>
            r.id === rideId ? { ...r, status: 'CANCELLED' } : r
          )
        );
        this.closePopup();
      },
      error: (err) => {
        this.closePopup();
        // todo: show error popup
      },
    });
  }

  closePopup(): void {
    this.showConfirmPopup = false;
    this.ridePendingCancel = null;
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
