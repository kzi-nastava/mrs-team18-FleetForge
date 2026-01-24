import { Component, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MapComponent } from '../map/map';
import { RideTrackingDTO } from '../dtos/ride-tracking.dtos';

export interface ActionButton {
  label: string;
  color: 'primary' | 'warn' | 'accent' | 'success';
  disabled?: boolean;
  action: string;
}

export interface CardInfo {
  label: string;
  name: string;
  rating?: number;
  phoneNumber: string;
  profileImage: string;
}

@Component({
  selector: 'app-current-ride',
  standalone: true,
  imports: [CommonModule, MapComponent, FormsModule],
  templateUrl: './current-ride.component.html',
  styleUrls: ['./current-ride.component.css']
})
export class CurrentRideComponent {
  @ViewChild(MapComponent) mapComponent!: MapComponent;

  @Input() rideData: RideTrackingDTO | null = null;
  @Input() cardInfo: CardInfo | null = null;
  @Input() actionButtons: ActionButton[] = [];
  @Input() showRating: boolean = true;

  @Output() routeCalculated = new EventEmitter<{distanceKm: number, estimatedMinutes: number}>();
  @Output() routeCoordinatesReceived = new EventEmitter<Array<{latitude: number, longitude: number}>>();
  @Output() actionButtonClicked = new EventEmitter<string>();

  calculatedDistance: number = 0;
  calculatedTime: number = 0;

  onRouteCalculated(routeInfo: {distanceKm: number, estimatedMinutes: number}): void {
    this.calculatedDistance = routeInfo.distanceKm;
    this.calculatedTime = routeInfo.estimatedMinutes;
    
    if (this.rideData) {
      this.rideData.route.totalDistanceKm = routeInfo.distanceKm;
      this.rideData.estimatedArrivalMinutes = routeInfo.estimatedMinutes;
    }

    this.routeCalculated.emit(routeInfo);
  }

  onRouteCoordinatesReceived(coordinates: Array<{latitude: number, longitude: number}>): void {
    this.routeCoordinatesReceived.emit(coordinates);
  }

  onActionButtonClicked(action: string): void {
    this.actionButtonClicked.emit(action);
  }

  getButtonClass(button: ActionButton): string {
    const baseClass = 'btn';
    const colorClass = `${button.color}-btn`;
    return `${baseClass} ${colorClass}`;
  }
}
