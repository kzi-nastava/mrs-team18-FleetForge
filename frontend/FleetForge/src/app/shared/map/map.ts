import { Component, AfterViewInit, Input, EventEmitter, Output } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import { VehicleLocationDTO } from '../models/vehicle.model';
import { NominatimService } from '../services/nominatim';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
  standalone: true
})
export class MapComponent implements AfterViewInit {
  private _vehicles: VehicleLocationDTO[] = [];
  private markers: L.Marker[] = [];
  private startPoint = L.latLng(45.2454929618196, 19.836663741992545);
  private endPoint = L.latLng(45.2462264156403, 19.852365232320707);
  
  @Output() mapClick = new EventEmitter<{address:string, lat:number, lng:number}>();
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
  
  public map!: L.Map;
  private vehicleMarkers: L.Marker[] = [];

  constructor(private nominatimService: NominatimService) {}

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

    this.registerOnClick();
    
    // Initialize routing after map is created
    this.setRoute();
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
     let DefaultIcon = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.6.0/dist/images/marker-icon.png',
    });

    L.Marker.prototype.options.icon = DefaultIcon;
    this.initMap();
    
    if (this.vehicles.length > 0) {
      this.displayVehicles();
    }
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

  setMarker(address: string): void {
    this.nominatimService.search(address+" Novi Sad").subscribe((data) => {
      console.log('Geocoding data:', data);
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

  setMarkerWithCoords(address: string, lat: number, lng: number): void {
    const newMarker = L.marker([lat, lng]);
    (newMarker as any).customAddress = address;
    newMarker.addTo(this.map);
    this.markers.push(newMarker);
    newMarker.bindPopup(address).openPopup();
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

  setRoute(): void {
    const routeControl = L.Routing.control({
      waypoints: [this.startPoint, this.endPoint],
      router: L.Routing.mapbox(environment.MAPBOX_API_KEY, {
        profile: 'mapbox/driving'
      }),
      collapsible: false,
      plan: L.Routing.plan([this.startPoint, this.endPoint], {
        addWaypoints: false
      })
    });

    routeControl.addTo(this.map);

    // Remove the routing panel
    const removePanel = () => {
      const container = document.querySelector('.leaflet-routing-container');
      if (container) container.remove();
    };

    removePanel();

  }

}