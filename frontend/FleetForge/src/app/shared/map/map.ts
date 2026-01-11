import { Component, AfterViewInit, Input } from '@angular/core';
import * as L from 'leaflet';
import { VehicleLocationDTO } from '../models/vehicle.model';

@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
  standalone: true
})
export class MapComponent implements AfterViewInit {
  private _vehicles: VehicleLocationDTO[] = [];
  
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
  
  private map!: L.Map;
  private vehicleMarkers: L.Marker[] = [];

  constructor() {}

  private initMap(): void {
    this.map = L.map('map', {
      center: [45.2396, 19.8227],
      zoom: 13,
    });

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
    this.initMap();
    
    if (this.vehicles.length > 0) {
      this.displayVehicles();
    }
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
        Status: <span font-weight: bold;">${availabilityText}</span>
      </div>
    `;

    const marker = L.marker([latitude, longitude], { icon: carIcon })
      .addTo(this.map)
      .bindPopup(popupContent);

    return marker;
  }

}
