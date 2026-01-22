import { Component, signal, WritableSignal } from '@angular/core';
import { Route } from '../../shared/models/route.model';
import { GeoPoint } from '../../shared/models/vehicle.model';
import { CommonModule } from '@angular/common';
import { RideFavoriteRoutesDTO } from '../../shared/dtos/ride.dtos';

@Component({
  selector: 'app-passenger-favorite-routes',
  imports: [CommonModule],
  templateUrl: './passenger-favorite-routes.component.html',
  styleUrl: './passenger-favorite-routes.component.css',
})
export class PassengerFavoriteRoutesComponent {

  protected routes:WritableSignal<RideFavoriteRoutesDTO[]> = signal<RideFavoriteRoutesDTO[]>([{
    id:1,
    startAddress:"Kneza Milosa 3",
    endAddress:"Kajmakcalska 5",
    waypoints:[{location: {latitude:45.2600, longitude:19.8400}, address: "Some address", orderIndex: 1},{location: {latitude:45.2610, longitude:19.8410}, address: "Some address 2", orderIndex: 2}]
  }
  ]);

  remove(route: RideFavoriteRoutesDTO): void {
    const updatedRoutes = this.routes().filter(r => r.id !== route.id);
    this.routes.set(updatedRoutes);
  }
  rideAgain(route: RideFavoriteRoutesDTO): void {
    console.log(`Riding again on route ID: ${route.id}`);
  }
}
