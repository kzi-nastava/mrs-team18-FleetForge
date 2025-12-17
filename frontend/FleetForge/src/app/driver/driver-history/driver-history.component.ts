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
  feedback: number; // 0-5 stars, 0 means waiting
}

@Component({
  selector: 'app-driver-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './driver-history.component.html',
  styleUrls: ['./driver-history.component.css']
})
export class DriverHistoryComponent {
  searchQuery: string = '';

  rides: Ride[] = [
    {
      name: 'Petar Petrovic',
      id: 'R123465798',
      pickupAddress: 'Kneza Milosa 3',
      dropoffAddress: 'Kajmakcalska 5',
      rideDate: '24 Oct, 2025',
      totalCost: 1500.00,
      cancellationStatus: 'Not cancelled',
      panicActivation: false,
      feedback: 0 // Waiting for feedback
    },
    {
      name: 'Petar Petrovic',
      id: 'R123465798',
      pickupAddress: 'Despota Stefana 4',
      dropoffAddress: 'Sekspiova 2',
      rideDate: '24 Oct, 2025',
      totalCost: 2500.00,
      cancellationStatus: 'By passenger',
      panicActivation: false,
      feedback: 5
    },
    {
      name: 'Petar Petrovic',
      id: 'R123465798',
      pickupAddress: 'Kozacinskog 1',
      dropoffAddress: 'Staljinova 10',
      rideDate: '18 Oct, 2025',
      totalCost: 450.00,
      cancellationStatus: 'By driver',
      panicActivation: false,
      feedback: 4
    },
    {
      name: 'Petar Petrovic',
      id: 'R123465798',
      pickupAddress: 'Mekinjeva 28',
      dropoffAddress: 'Mise Dimitrijevica 32',
      rideDate: '8 Oct, 2025',
      totalCost: 552.00,
      cancellationStatus: 'By passenger',
      panicActivation: true,
      feedback: 4
    }
  ];

  getStarArray(rating: number): boolean[] {
    return Array(5).fill(false).map((_, index) => index < rating);
  }
}
