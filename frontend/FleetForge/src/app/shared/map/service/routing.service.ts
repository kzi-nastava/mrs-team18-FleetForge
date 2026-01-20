import { Injectable } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RoutingService {

  constructor(private http: HttpClient) {}

  addRoute(
  map: L.Map,
  start: L.LatLngExpression,
  end: L.LatLngExpression,
  waypoints: L.LatLngExpression[] = []
): L.Routing.Control {

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
      createMarker: () => false 
    }),
    lineOptions: {
      addWaypoints: false,      
      extendToWaypoints: true,
      missingRouteTolerance: 0
    }
  }).addTo(map);

  routeControl.on('routesfound', (e: any) => {
    const route = e.routes[0];
    const distanceKm = +(route.summary.totalDistance / 1000).toFixed(2);

    this.http.post('http://localhost:8080/api/ride-estimates', { distanceKm })
      .subscribe({
        next: res => console.log('Ride estimate:', res),
        error: err => console.error('Estimate error:', err)
      });
  });

  const panel = document.querySelector('.leaflet-routing-container');
  if (panel) (panel as HTMLElement).style.display = 'none';

  return routeControl;
}
}
