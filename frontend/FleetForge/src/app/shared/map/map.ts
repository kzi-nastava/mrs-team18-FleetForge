import { Component, AfterViewInit, Input, EventEmitter, Output } from '@angular/core';
import * as L from 'leaflet';
import { VehicleLocationDTO } from '../models/vehicle.model';
import { NominatimService } from '../services/nominatim';
import { RoutingService } from './service/routing.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
  standalone: true
})
export class MapComponent implements AfterViewInit {
  private _vehicles: VehicleLocationDTO[] = [];
  private markers: L.Marker[] = [];
  private locationMarkers: Map<string, L.Marker> = new Map();
  private routeControl?: L.Routing.Control;
  
  @Output() mapClick = new EventEmitter<{address:string, lat:number, lng:number}>();
  @Output() routeSummary = new EventEmitter<{ distanceKm: number; durationMin: number; cost: number }>();
  @Input() set vehicles(value: VehicleLocationDTO[]) {
    this._vehicles = value;
    console.log('Vehicles input changed:', value);
    if (this.map) {
      this.displayVehicles();
    }
  }
  
  get vehicles(): VehicleLocationDTO[] {
    return this._vehicles;
  }

  clearRoute(): void {
    if (this.routeControl) {
      this.map.removeControl(this.routeControl);
      this.routeControl = undefined;
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

  registerOnClick(): void {
    this.map.on('click', (e: any) => {
      const coord = e.latlng;
      const lat = coord.lat;
      const lng = coord.lng;
      console.log(
        'You clicked the map at latitude: ' + lat + ' and longitude: ' + lng
      );
      this.nominatimService.reverseSearch(lat, lng).subscribe((data) => {
        if(data.address.house_number==undefined){
          this.mapClick.emit({address: data.address.road, lat: lat, lng: lng});
        }else{
          this.mapClick.emit({address: data.address.road + ' ' + data.address.house_number, lat: lat, lng: lng});
        }
      });
    });
  }

 setMarker(address: string): Observable<void> {
  return new Observable(observer => {
    this.nominatimService.search(address + " Novi Sad").subscribe((data) => {
      console.log('Geocoding data:', data);
      if (data && data.length > 0) {
        const lat = parseFloat(data[0].lat);
        const lon = parseFloat(data[0].lon);
        const newMarker = L.marker([lat, lon]);
        this.locationMarkers.set(address, newMarker);
      
        (newMarker as any).customAddress = address;
        newMarker.addTo(this.map);
        this.markers.push(newMarker);
        newMarker.bindPopup(address).openPopup();
        
        observer.next();
        observer.complete();
      } else {
        observer.error('No results found');
      }
    });
  });
}
  
  removeMarker(address: string): void {
    this.markers = this.markers.filter(marker => {
      if ((marker as any).customAddress === address) {
        this.map.removeLayer(marker); 
        this.locationMarkers.delete(address);
        return false; 
      }
      return true; 
    });
  }

  setMarkerWithCoords(address: string, lat: number, lng: number): void{
    const newMarker = L.marker([lat, lng]);
    (newMarker as any).customAddress = address;
    newMarker.addTo(this.map);
    this.markers.push(newMarker);
    this.locationMarkers.set(address, newMarker);
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
      this.routeControl = undefined;
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
    console.log('displayVehicles called. Map exists:', !!this.map, 'Vehicles count:', this.vehicles.length);
    if (!this.map) return;

    this.vehicleMarkers.forEach(marker => marker.remove());
    this.vehicleMarkers = [];

    this.vehicles.forEach(vehicle => {
      console.log('Processing vehicle:', vehicle);
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