import * as L from 'leaflet';
import 'leaflet-routing-machine';
import { environment } from '../../../../environments/environment';

export class RoutingService {
  static addRoute(
    map: L.Map,
    start: L.LatLngExpression,
    end: L.LatLngExpression,
    waypoints: L.LatLngExpression[] = []
  ): L.Routing.Control {
    // Convert all points to L.LatLng first, then to waypoints
    const allWaypoints: L.Routing.Waypoint[] = [
      L.Routing.waypoint(L.latLng(start)),
      ...waypoints.map(wp => L.Routing.waypoint(L.latLng(wp))),
      L.Routing.waypoint(L.latLng(end)),
    ];

    const routeControl = L.Routing.control({
      waypoints: allWaypoints,
      router: L.Routing.mapbox(environment.MAPBOX_API_KEY, { profile: 'mapbox/driving' }),
      collapsible: false,
      plan: L.Routing.plan(allWaypoints, { addWaypoints: false })
    }).addTo(map);

    // Remove default routing panel
    const panel = document.querySelector('.leaflet-routing-container');
    if (panel) panel.remove();

    return routeControl;
  }
}
