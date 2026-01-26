import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { Route } from '../../shared/models/route.model';
import { GeoPoint } from '../../shared/models/vehicle.model';
import { CommonModule } from '@angular/common';
import { RideFavoriteRoutesDTO } from '../../shared/dtos/ride.dtos';
import { PassengerFavorite } from '../service/passenger-favorite/passenger-favorite';
import { Router } from '@angular/router';

@Component({
  selector: 'app-passenger-favorite-routes',
  imports: [CommonModule],
  templateUrl: './passenger-favorite-routes.component.html',
  styleUrl: './passenger-favorite-routes.component.css',
})
export class PassengerFavoriteRoutesComponent implements OnInit {

  // protected routes:WritableSignal<RideFavoriteRoutesDTO[]> = signal<RideFavoriteRoutesDTO[]>([{
  //   id:1,
  //   startAddress:"Kneza Milosa 3",
  //   endAddress:"Kajmakcalska 5",
  //   waypoints:[{location: {latitude:45.2600, longitude:19.8400}, address: "Some address", orderIndex: 1},{location: {latitude:45.2610, longitude:19.8410}, address: "Some address 2", orderIndex: 2}],
  //   name: "Home to Work"
  
  // }
  // ]);
  protected routes:WritableSignal<RideFavoriteRoutesDTO[]> = signal<RideFavoriteRoutesDTO[]>([]);
constructor(private passengerFavorite: PassengerFavorite,private router: Router) {}
  ngOnInit(): void {
     this.passengerFavorite.getFavoriteRoutes().subscribe(routes => {
      this.routes.set(routes);
    });
  }
  remove(route: RideFavoriteRoutesDTO): void {
    this.passengerFavorite.deleteFavoriteRoute(route.id).subscribe(() => {
      const updatedRoutes = this.routes().filter(r => r.id !== route.id);
      this.routes.set(updatedRoutes);
    });
  }
  rideAgain(route: RideFavoriteRoutesDTO): void {
    this.router.navigate(['/passenger/passenger-home'], { state: { favoriteRoute: route } });
  }
}