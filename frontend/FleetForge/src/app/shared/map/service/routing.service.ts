import { Injectable } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import { environment } from '../../../../environments/environment';
import { Subject } from 'rxjs';
import { RideEstimateService } from '../../services/ride-estimate.service';
import { VehicleType } from '../../models/vehicle.model';


@Injectable({
  providedIn: 'root'
})
export class RoutingService {  
  routeSummary$ = new Subject<{ distanceKm: number; durationMin: number; cost: number }>();

  constructor(private rideEstimateService: RideEstimateService) {}

  estimateRidePrice(distanceKm: number, durationMin: number, vehicleType: string): void {
    this.rideEstimateService.estimateRide({ distanceKm, vehicleType: vehicleType as VehicleType })
      .subscribe({
        next: res => {
          this.routeSummary$.next({
            distanceKm,
            durationMin,
            cost: res.estimatedPrice   
          });
        },
        error: err => console.error('Estimate error:', err)
      });
  }

  addRoute(
  map: L.Map,
  start: L.LatLngExpression,
  end: L.LatLngExpression,
  waypoints: L.LatLngExpression[] = [],
  opts?: {
    color?: string;
    vehicleType?: VehicleType;
    createMarker?: (i: number, wp: L.Routing.Waypoint, n: number) => L.Marker | false;
  }
): L.Routing.Control {

  const vehicleType = opts?.vehicleType ?? VehicleType.STANDARD;

  const allWaypoints: L.Routing.Waypoint[] = [
    L.Routing.waypoint(L.latLng(start)),
    ...waypoints.map(wp => L.Routing.waypoint(L.latLng(wp))),
    L.Routing.waypoint(L.latLng(end)),
  ];

  const routeControl = L.Routing.control({
    waypoints: allWaypoints,
    router: L.Routing.mapbox(environment.MAPBOX_API_KEY, {
      profile: 'mapbox/driving'
    }),
    collapsible: false,
    plan: L.Routing.plan(allWaypoints, {
      addWaypoints: false,
      draggableWaypoints: false,
      createMarker: (i, wp, n) => opts?.createMarker ? opts.createMarker(i, wp, n) : false
    }),
    lineOptions: {
      addWaypoints: false,      
      extendToWaypoints: true,
      missingRouteTolerance: 0,
      styles: [{ color: opts?.color ?? '#FF9900', weight: 4, opacity: 0.9 }]
    }
  }).addTo(map);

  routeControl.on('routesfound', (e: any) => {
    const route = e.routes[0];

    const distanceKm = +(route.summary.totalDistance / 1000).toFixed(2);
    const durationMin = +(route.summary.totalTime / 60).toFixed(1);

    this.rideEstimateService.estimateRide({ distanceKm, vehicleType })
      .subscribe({
        next: res => {
          this.routeSummary$.next({
            distanceKm,
            durationMin,
            cost: res.estimatedPrice   
          });
        },
        error: err => console.error('Estimate error:', err)
      });
  });


    const panel = document.querySelector('.leaflet-routing-container');
    if (panel) (panel as HTMLElement).style.display = 'none';

    return routeControl;
  }
}
