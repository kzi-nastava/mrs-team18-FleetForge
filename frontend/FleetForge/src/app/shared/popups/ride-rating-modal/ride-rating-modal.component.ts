import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface RatingFormData {
  driverRating: number;
  vehicleRating: number;
  comment: string;
}

@Component({
  selector: 'app-ride-rating-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ride-rating-modal.component.html',
  styleUrls: ['./ride-rating-modal.component.css'],
})
export class RideRatingModalComponent {
  @Input() isOpen = false;
  @Input() pickupAddress = '';
  @Input() dropoffAddress = '';
  @Input() isLoading = false;
  @Input() showFinishedMessage = false;
  @Input() formData: RatingFormData = {
    driverRating: 0,
    vehicleRating: 0,
    comment: '',
  };

  @Output() closed = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<RatingFormData>();
  @Output() notNow = new EventEmitter<void>();

  readonly starScale = [1, 2, 3, 4, 5];

  hoverDriverRating = 0;
  hoverVehicleRating = 0;

  onClose(): void {
    this.closed.emit();
  }

  onSubmit(): void {
    if (this.formData.driverRating === 0 || this.formData.vehicleRating === 0) {
      return;
    }
    this.submitted.emit(this.formData);
  }

  onNotNow(): void {
    this.notNow.emit();
  }

  setDriverRating(value: number): void {
    this.formData.driverRating = value;
  }

  setVehicleRating(value: number): void {
    this.formData.vehicleRating = value;
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

  getStarArray(rating: number): boolean[] {
    const safeRating = Math.max(0, Math.min(5, Math.floor(rating)));
    return Array(5)
      .fill(false)
      .map((_, index) => index < safeRating);
  }
}
