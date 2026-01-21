import { Component, AfterViewInit, Input, EventEmitter, Output, OnDestroy } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import { VehicleLocationDTO } from '../models/vehicle.model';
import { NominatimService } from '../services/nominatim';
import { RideTrackingDTO } from '../dtos/ride-tracking.dtos';
import { environment } from '../../../environments/environment';
import { RoutingService } from './service/routing.service';


@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
  standalone: true
})
export class MapComponent implements AfterViewInit, OnDestroy {
  private _vehicles: VehicleLocationDTO[] = [];
  private _currentRide: RideTrackingDTO | null = null;
  private markers: L.Marker[] = [];

  private routeControl: L.Routing.Control | null = null;
  private locationMarkers: Map<string, L.Marker> = new Map();
  private currentLocationMarker: L.Marker | null = null;
  private trackingInterval: any;
  
  @Output() mapClick = new EventEmitter<{address:string, lat:number, lng:number}>();
  @Output() routeCalculated = new EventEmitter<{distanceKm: number, estimatedMinutes: number}>();
  @Output() routeCoordinatesAvailable = new EventEmitter<Array<{latitude: number, longitude: number}>>();  
  @Output() routeSummary = new EventEmitter<{ distanceKm: number; durationMin: number; cost: number }>();
  @Input() set vehicles(value: VehicleLocationDTO[]) {
    this._vehicles = value;
    if (this.map) {
      this.displayVehicles();
    }
  }
  
  @Input() set currentRide(value: RideTrackingDTO | null) {
    this._currentRide = value;
    if (this.map && value) {
      this.displayCurrentRide();
    } else {
    }
  }
  
  get vehicles(): VehicleLocationDTO[] {
    return this._vehicles;
  }

  clearRoute(): void {
    if (this.routeControl) {
      this.map.removeControl(this.routeControl);
      this.routeControl = null;
    }
  }
  
  public map!: L.Map;
  private vehicleMarkers: L.Marker[] = [];

  constructor(
    private nominatimService: NominatimService,
    private routingService: RoutingService
  ) {}

  private initMap(): void {
    this.map = L.map('map', {
      center: [45.2396, 19.8227],
      zoom: 13,
    });
    
    this.map.attributionControl.setPrefix(`Leaflet`);

    const tiles = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        maxZoom: 18,
        minZoom: 3,
        attribution:
          '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      }
    );
    tiles.addTo(this.map);

    this.map.on('zoom', () => {
      this.updateMarkerSizes();
    });

    this.registerOnClick();
  
    if (this._currentRide) {
      this.displayCurrentRide();
    }
  }

  private updateMarkerSizes(): void {
    const zoom = this.map.getZoom();
    const iconSize = 30 + (zoom - 10) * 3; 
    
    this.vehicleMarkers.forEach(marker => {
      const icon = marker.getIcon() as L.DivIcon;
      icon.options.iconSize = [iconSize, iconSize];
      icon.options.iconAnchor = [iconSize / 2, iconSize / 2];
      marker.setIcon(icon);
    });
  }

  ngAfterViewInit(): void {
    const DefaultIcon = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.6.0/dist/images/marker-icon.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.6.0/dist/images/marker-shadow.png',
      iconSize: [25, 41],      
      iconAnchor: [12, 41],    
      popupAnchor: [1, -34],   
      shadowSize: [41, 41]     
    });

    L.Marker.prototype.options.icon = DefaultIcon;
    this.initMap();
    
    if (this.vehicles.length > 0) {
      this.displayVehicles();
    }

    this.routingService.routeSummary$.subscribe(summary => {
      this.routeSummary.emit(summary);
    });

  }

  ngOnDestroy(): void {
    if (this.trackingInterval) {
      clearInterval(this.trackingInterval);
    }
  }

  registerOnClick(): void {
    this.map.on('click', (e: any) => {
      const coord = e.latlng;
      const lat = coord.lat;
      const lng = coord.lng;
      this.nominatimService.reverseSearch(lat, lng).subscribe((data) => {
        if(data.address.house_number==undefined){
          this.mapClick.emit({address: data.address.road, lat: lat, lng: lng});
        }else{
          this.mapClick.emit({address: data.address.road + ' ' + data.address.house_number, lat: lat, lng: lng});
        }
      });
    });
  }

  private displayCurrentRide(): void {
    if (!this._currentRide || !this.map) {
      return;
    }

    // Remove previous route if exists
    if (this.routeControl) {
      this.map.removeControl(this.routeControl);
      this.routeControl = null;
    }

    this.markers.forEach(marker => this.map.removeLayer(marker));
    this.markers = [];

    const route = this._currentRide.route;
    
    const remainingWaypoints = route.waypoints
    .filter(wp => !wp.isCompleted)
    .sort((a, b) => a.order - b.order)
    .map(wp => L.latLng(wp.location.latitude, wp.location.longitude));

    const waypoints: L.LatLng[] = [
      L.latLng(this._currentRide.currentLocation.latitude, this._currentRide.currentLocation.longitude),
      ...remainingWaypoints,
      L.latLng(route.endLocation.latitude, route.endLocation.longitude)
    ];

    this.routeControl = L.Routing.control({
      waypoints: waypoints,
      router: L.Routing.mapbox(environment.MAPBOX_API_KEY, { 
        profile: 'mapbox/driving' 
      }),
      routeWhileDragging: false,
      addWaypoints: false,
      lineOptions: {
        styles: [{ color: '#FF9900', weight: 4, opacity: 0.8 }],
        extendToWaypoints: true,
        missingRouteTolerance: 0
      },
      show: false,
      createMarker: function() { return null; }
    } as any).addTo(this.map);

    // Listen for route calculation
    this.routeControl.on('routesfound', (e: any) => {
      const routes = e.routes;
      const summary = routes[0].summary;
      const distanceKm = summary.totalDistance / 1000;
      const estimatedMinutes = Math.round(summary.totalTime / 60);

      const coordinates = routes[0].coordinates.map((coord: any) => ({
        latitude: coord.lat,
        longitude: coord.lng
      }));
      
      this.routeCalculated.emit({ distanceKm, estimatedMinutes });
      this.routeCoordinatesAvailable.emit(coordinates);
    });

    this.addRouteMarkers(route.startLocation, route.startAddress, 'start');
    
    route.waypoints.forEach((wp, index) => {
      this.addRouteMarkers(wp.location, wp.address, 'waypoint', index + 1);
    });
    
    this.addRouteMarkers(route.endLocation, route.endAddress, 'end');
    this.updateCurrentLocationMarker(this._currentRide.currentLocation);

    const bounds = L.latLngBounds(waypoints);
    this.map.fitBounds(bounds, { padding: [50, 50] });
    
  }

  private addRouteMarkers(location: {latitude: number, longitude: number}, address: string, type: 'start' | 'waypoint' | 'end', order?: number): void {
    let iconHtml = '';
    
    if (type === 'start') {
      iconHtml = '<img src="/map-pointer.svg" style="width: 28px; height: 28px; filter: invert(65%) sepia(74%) saturate(1200%) hue-rotate(65deg);" />';
    } else if (type === 'end') {
      iconHtml = '<img src="/map-pointer.svg" style="width: 28px; height: 28px; filter: invert(35%) sepia(74%) saturate(1200%) hue-rotate(340deg);" />';
    } else {
      iconHtml = '<img src="/waypoint-circle.svg" style="width: 28px; height: 28px;" />';
    }

    const icon = L.divIcon({
      html: iconHtml,
      className: 'custom-marker-icon',
      iconSize: [28, 28],
      iconAnchor: [14, 28]
    });

    const marker = L.marker([location.latitude, location.longitude], { icon })
      .addTo(this.map)
      .bindPopup(address);
    
    this.markers.push(marker);
  }

  private updateCurrentLocationMarker(location: {latitude: number, longitude: number}): void {
    if (this.currentLocationMarker) {
      this.currentLocationMarker.setLatLng([location.latitude, location.longitude]);
    } else {
      const carIconHtml = `
        <div style="background: #FF9900; border-radius: 50%; width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 8px rgba(0,0,0,0.3);">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="white" xmlns="http://www.w3.org/2000/svg">
            <path d="M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1h12v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-8l-2.08-5.99zM6.85 7h10.29l1.08 3.11H5.77L6.85 7zM19 17H5v-5h14v5z"/>
            <circle cx="7.5" cy="14.5" r="1.5"/>
            <circle cx="16.5" cy="14.5" r="1.5"/>
          </svg>
        </div>
      `;

      const carIcon = L.divIcon({
        html: carIconHtml,
        className: 'current-location-marker',
        iconSize: [40, 40],
        iconAnchor: [20, 20]
      });

      this.currentLocationMarker = L.marker([location.latitude, location.longitude], { icon: carIcon })
        .addTo(this.map)
        .bindPopup('Current Location');
    }
  }

  setMarker(address: string): void {
    this.nominatimService.search(address+" Novi Sad").subscribe((data) => {
      if (data && data.length > 0) {
        const lat = parseFloat(data[0].lat);
        const lon = parseFloat(data[0].lon);
        const newMarker = L.marker([lat, lon]);
      
        (newMarker as any).customAddress = address;
        newMarker.addTo(this.map);
        this.markers.push(newMarker);
        newMarker.bindPopup(address).openPopup();
      }
    });
  }
  
  removeMarker(address: string): void {
    this.markers = this.markers.filter(marker => {
      if ((marker as any).customAddress === address) {
        this.map.removeLayer(marker); 
        return false; 
      }
      return true; 
    });
  }

  updateCurrentLocation(location: {latitude: number, longitude: number}): void {
    if (!this._currentRide || !this.map) {
      return;
    }

    this.updateCurrentLocationMarker(location);
    if (this.routeControl) {
      const route = this._currentRide.route;
      
      const remainingWaypoints = route.waypoints
        .filter(wp => !wp.isCompleted)
        .sort((a, b) => a.order - b.order)
        .map(wp => L.latLng(wp.location.latitude, wp.location.longitude));

      const waypoints: L.LatLng[] = [
        L.latLng(location.latitude, location.longitude),
        ...remainingWaypoints,
        L.latLng(route.endLocation.latitude, route.endLocation.longitude)
      ];

      this.routeControl.setWaypoints(waypoints);
    }
  }

  setMarkerWithCoords(address: string, lat: number, lng: number): void {
    const newMarker = L.marker([lat, lng]);
    (newMarker as any).customAddress = address;
    newMarker.addTo(this.map);
    this.markers.push(newMarker);
    newMarker.bindPopup(address).openPopup();
  }

  setLocationMarker(id: string, type: 'pickup' | 'waypoint' | 'dropoff', label: string, lat: number, lon: number): void {
    if (this.locationMarkers.has(id)) {
      const existingMarker = this.locationMarkers.get(id);
      this.map.removeLayer(existingMarker!);
      this.locationMarkers.delete(id);
    }

    const iconHtml = this.getLocationIconHtml(type);
    
    const locationIcon = L.divIcon({
      html: iconHtml,
      className: 'location-marker-icon',
      iconSize: [32, 32],
      iconAnchor: [16, 32],
      popupAnchor: [0, -32],
    });

    const marker = L.marker([lat, lon], { icon: locationIcon })
      .addTo(this.map)
      .bindPopup(`<strong>${this.getTypeLabel(type)}</strong><br/>${label}`);

    this.locationMarkers.set(id, marker);

    this.map.panTo([lat, lon]);
  }

  removeLocationMarker(id: string): void {
    if (this.locationMarkers.has(id)) {
      const marker = this.locationMarkers.get(id);
      this.map.removeLayer(marker!);
      this.locationMarkers.delete(id);
    }
  }

  updateRoute(
    pickup: [number, number],
    dropoff: [number, number],
    waypoints: [number, number][] = []
  ): void {
    if (this.routeControl) {
      this.map.removeControl(this.routeControl);
      this.routeControl = null;
    }

    const waypointLatLngs = waypoints.map(wp => L.latLng(wp[0], wp[1]));
    
    this.routeControl = this.routingService.addRoute(
      this.map,
      L.latLng(pickup[0], pickup[1]),
      L.latLng(dropoff[0], dropoff[1]),
      waypointLatLngs
    );
  }

  private getLocationIconHtml(type: 'pickup' | 'waypoint' | 'dropoff'): string {
    const colors = {
      pickup: '#ef4444',  // red
      waypoint: '#3b82f6',  // blue
      dropoff: '#22c55e'    // green
    };

    const color = colors[type];

    return `
      <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z" 
              fill="${color}" 
              stroke="white" 
              stroke-width="1.5"/>
        <circle cx="12" cy="9" r="2.5" fill="white"/>
      </svg>
    `;
  }

  private getTypeLabel(type: 'pickup' | 'waypoint' | 'dropoff'): string {
    const labels = {
      pickup: 'Pickup',
      waypoint: 'Waypoint',
      dropoff: 'Dropoff'
    };
    return labels[type];
  }

  private displayVehicles(): void {
    if (!this.map) return;

    this.vehicleMarkers.forEach(marker => marker.remove());
    this.vehicleMarkers = [];

    this.vehicles.forEach(vehicle => {
      if (vehicle.isActive) {
        const marker = this.createVehicleMarker(vehicle);
        this.vehicleMarkers.push(marker);
      }
    });
  }

  private createVehicleMarker(vehicle: VehicleLocationDTO): L.Marker {
    const { latitude, longitude } = vehicle.currentLocation;
    const isAvailable = vehicle.isAvailable;
    
    const iconUrl = isAvailable ? '/green-car-icon.svg' : '/red-car-icon.svg';

    const html = `
      <img src="${iconUrl}" alt="vehicle" style="width: 40px; height: 40px;">
    `;

    const carIcon = L.divIcon({
      html: html,
      className: 'car-marker-icon',
      iconSize: [40, 40],
      iconAnchor: [20, 20],
      popupAnchor: [0, -20],
    });

    const availabilityText = isAvailable ? 'Available' : 'Occupied';
    const popupContent = `
      <div style="font-family: var(--font-primary);">
        <strong>${vehicle.model}</strong><br/>
        Type: ${vehicle.vehicleType}<br/>
        Status: <span style="font-weight: bold;">${availabilityText}</span>
      </div>
    `;

    const marker = L.marker([latitude, longitude], { icon: carIcon })
      .addTo(this.map)
      .bindPopup(popupContent);

    return marker;
  }
}