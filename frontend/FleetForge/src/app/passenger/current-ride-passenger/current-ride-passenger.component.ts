import { Component, OnInit, OnDestroy, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CurrentRideComponent, ActionButton, CardInfo } from '../../shared/current-ride/current-ride.component';
import { RideTrackingDTO } from '../../shared/dtos/ride-tracking.dtos';
import { PassengerCurrentRide } from '../../passenger/service/passenger-current-ride/passenger-current-ride';
import { interval, Subscription, of } from 'rxjs';
import { catchError, startWith, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-current-ride-passenger',
  standalone: true,
  imports: [CommonModule, CurrentRideComponent, FormsModule],
  templateUrl: './current-ride-passenger.component.html',
  styleUrls: ['./current-ride-passenger.component.css']
})
export class CurrentRidePassengerComponent implements OnInit, OnDestroy {
  @ViewChild(CurrentRideComponent) currentRideComponent!: CurrentRideComponent;
  
  rideData: RideTrackingDTO | null = null;
  cardInfo: CardInfo | null = null;
  actionButtons: ActionButton[] = [
    { label: 'Wrong way', color: 'primary', action: 'wrong-way' },
    { label: 'SOS', color: 'warn', action: 'sos' }
  ];
  
  private trackingSub?: Subscription;
  
  showWrongWayModal: boolean = false;
  wrongWayReport: string = '';
  
  constructor(
    private passengerCurrentRideService: PassengerCurrentRide,
    private cdr: ChangeDetectorRef
  ) {}
  
  ngOnInit(): void {
    this.startTrackingPoll();
  }

  ngOnDestroy(): void {
    this.clearTracking();
  }

  private clearTracking() {
    if (this.trackingSub) {
      this.trackingSub.unsubscribe();
      this.trackingSub = undefined;
    }
  }

  get activeActionButtons(): ActionButton[] {
    if (this.isInProgress()) {
      return this.actionButtons;
    }
    //TODO - after ride is completed, allow feedback submission 
    return [];
  }

  private setCardInfoFromRide(): void {
    if (this.rideData && this.rideData.driver) {
      const driver = this.rideData.driver;
      this.cardInfo = {
        label: 'Driver',
        name: `${driver.firstName} ${driver.lastName}`,
        rating: 4.8,
        phoneNumber: driver.phoneNumber,
        profileImage: driver.profileImage
      };
    } else {
      this.cardInfo = null;
    }
  }

  private startTrackingPoll(): void {
    this.trackingSub = interval(1500)
      .pipe(
        startWith(0),
        switchMap(() => this.passengerCurrentRideService.getActiveTracking()
          .pipe(
            catchError(err => {
              // If no active ride (404), clear state; keep polling.
              if (err?.status === 404) {
                this.rideData = null;
                this.cardInfo = null;
                this.clearTracking();
                this.cdr.detectChanges();
              }
              return of(null);
            })
          )
        )
      )
      .subscribe((data) => {
        if (!data) return;
        this.rideData = data as RideTrackingDTO;
        this.setCardInfoFromRide();

        this.cdr.detectChanges();

        if (this.rideData?.currentLocation) {
          this.currentRideComponent?.mapComponent?.updateCurrentLocation(this.rideData.currentLocation);
        }
        
        if (this.isCompleted()) {
          this.trackingSub?.unsubscribe();
          this.trackingSub = undefined;
        }
      });
  }

  onActionButton(action: string): void {
    if (action === 'wrong-way') this.showWrongWayModal = true;
    if (action === 'sos') console.log('SOS triggered');
  }

  onCloseWrongWayModal(): void { this.showWrongWayModal = false; }
  
  onSubmitWrongWayReport(): void {
    if (this.wrongWayReport.trim()) this.onCloseWrongWayModal();
  }

  hasDriver(): boolean {
    return !!this.rideData?.driver;
  }

  isAccepted(): boolean {
    return this.rideData?.status === 'ACCEPTED';
  }

  isInProgress(): boolean {
    return this.rideData?.status === 'IN_PROGRESS';
  }

  isCompleted(): boolean {
    return this.rideData?.status === 'COMPLETED';
  }

  getRideSubtitle(): string {
    if (!this.rideData) return '';

    if (this.isAccepted() && !this.hasDriver()) {
      return 'We will assign you a driver in a moment';
    }

    if (this.isAccepted() && this.hasDriver()) {
      return 'Driver is coming to your pickup location';
    }

    if (this.isInProgress()) {
      return 'Enjoy your ride — live tracking enabled';
    }

    if (this.isCompleted()) {
      return 'Ride completed';
    }

    return '';
  }
}