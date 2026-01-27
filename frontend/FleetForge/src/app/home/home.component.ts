import { Component, OnInit, ChangeDetectorRef, OnDestroy, ViewChild } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MapComponent } from '../shared/map/map';
import { VehicleService } from '../shared/services/vehicle.service';
import { VehicleLocationDTO } from '../shared/models/vehicle.model';
import { PhotonFeature, PhotonService } from '../shared/services/photon.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';

interface LocationField {
  id: string;
  type: 'pickup' | 'waypoint' | 'dropoff';
  query: string;
  suggestions: PhotonFeature[];
  showSuggestions: boolean;
  searchSubject: Subject<string>;
  coordinates?: { lat: number; lon: number };
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterModule,
    MapComponent,
    CommonModule,
    FormsModule
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit, OnDestroy {
  @ViewChild(MapComponent) mapComponent!: MapComponent;
  
  vehicles: VehicleLocationDTO[] = [];
  
  locationFields: LocationField[] = [];
  
  private subscriptions: Subscription[] = [];
  private nextWaypointId = 0;
  private readonly STORAGE_KEY = 'location_fields';

  estimatedDistance?: number;
  estimatedDuration?: number;
  estimatedCost?: number;


  constructor(
    private vehicleService: VehicleService,
    private photonService: PhotonService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadVehicles();
    this.restoreState();
  }

  clearSavedLocations(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    location.reload();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.locationFields.forEach(field => field.searchSubject.complete());
  }

  onRouteSummary(summary: { distanceKm: number; durationMin: number; cost: number }): void {
    this.estimatedDistance = summary.distanceKm;
    this.estimatedDuration = summary.durationMin;
    this.estimatedCost = summary.cost;
  }

  orderRide(): void {
    this.router.navigate(['/login']);
  }

  private persistState(): void {
    const serializable = this.locationFields.map(f => ({
      id: f.id,
      type: f.type,
      query: f.query,
      coordinates: f.coordinates
    }));

    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(serializable));
  }


  private restoreState(): void {
    const raw = localStorage.getItem(this.STORAGE_KEY);

    if (!raw) {
      this.initializeLocationFields();
      return;
    }

    const saved = JSON.parse(raw);

    this.locationFields = [];
    this.nextWaypointId = 0;

    saved.forEach((s: any) => {
      const field: LocationField = {
        id: s.id,
        type: s.type,
        query: s.query,
        suggestions: [],
        showSuggestions: false,
        searchSubject: new Subject<string>(),
        coordinates: s.coordinates
      };

      this.setupSearchStream(field);

      if (s.type === 'waypoint') {
        const num = parseInt(s.id.split('-')[1] || '0', 10);
        this.nextWaypointId = Math.max(this.nextWaypointId, num + 1);
      }

      this.locationFields.push(field);
    });

    this.cdr.markForCheck();

    // Restore markers first, then try to create route
    setTimeout(() => {
      this.locationFields.forEach(f => {
        if (f.coordinates && this.mapComponent) {
          this.mapComponent.setLocationMarker(
            f.id,
            f.type,
            f.query,
            f.coordinates.lat,
            f.coordinates.lon
          );
        }
      });

      this.updateRoute();
      this.persistState();
    });
  }


  private initializeLocationFields(): void {
    this.addLocationField('pickup');
    this.addLocationField('dropoff');
  }

  private addLocationField(type: 'pickup' | 'waypoint' | 'dropoff'): LocationField {
    const id = type === 'waypoint' 
      ? `waypoint-${this.nextWaypointId++}` 
      : type;

    const field: LocationField = {
      id,
      type,
      query: '',
      suggestions: [],
      showSuggestions: false,
      searchSubject: new Subject<string>()
    };

    this.setupSearchStream(field);
    
    if (type === 'waypoint') {
      const dropoffIndex = this.locationFields.findIndex(f => f.type === 'dropoff');
      this.locationFields.splice(dropoffIndex, 0, field);
    } else {
      this.locationFields.push(field);
    }

    return field;
  }

  private setupSearchStream(field: LocationField): void {
    const subscription = field.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((query: string) => {
        if (query.length < 2) {
          field.suggestions = [];
          field.showSuggestions = false;
          this.cdr.markForCheck();
          return [];
        }
        return this.photonService.searchSuggestions(query);
      })
    ).subscribe({
      next: (features: PhotonFeature[]) => {
        field.suggestions = features;
        field.showSuggestions = features.length > 0;
        this.cdr.markForCheck();
      },
      error: (error: any) => {
        console.error(`Error fetching ${field.type} suggestions:`, error);
        field.suggestions = [];
        field.showSuggestions = false;
        this.cdr.markForCheck();
      }
    });

    this.subscriptions.push(subscription);
  }

  loadVehicles(): void {
    this.vehicleService.getActiveVehicles().subscribe({
      next: (vehicles) => {
        this.vehicles = [...vehicles]; 
        this.cdr.markForCheck();
        console.log('Loaded vehicles:', this.vehicles);
      },
      error: (error) => {
        console.error('Error loading vehicles:', error);
      }
    });
  }

  addWaypoint(): void {
    this.addLocationField('waypoint');
    this.cdr.markForCheck();
    this.persistState();
  }

  removeWaypoint(waypointId: string): void {
    const index = this.locationFields.findIndex(f => f.id === waypointId);
    if (index !== -1) {
      const field = this.locationFields[index];
      field.searchSubject.complete();
      
      if (field.coordinates && this.mapComponent) {
        this.mapComponent.removeLocationMarker(field.id);
      }
      
      this.locationFields.splice(index, 1);
      
      this.updateRoute();
      this.persistState();
      this.cdr.markForCheck();
    }
  }

  onLocationInput(field: LocationField): void {
    if (!field.query || field.query.trim().length === 0) {
      this.clearFieldLocation(field);
      return;
    }

    field.searchSubject.next(field.query);
    this.persistState();
  }

  private clearFieldLocation(field: LocationField): void {
    field.query = '';
    field.coordinates = undefined;
    field.suggestions = [];
    field.showSuggestions = false;

    if (this.mapComponent) {
      this.mapComponent.removeLocationMarker(field.id);
    }

    this.updateRoute();
    this.persistState();
    this.cdr.markForCheck();
  }


  selectSuggestion(field: LocationField, feature: PhotonFeature): void {
    const displayName = this.getDisplayName(feature);
    field.query = displayName;
    field.suggestions = [];
    field.showSuggestions = false;
    
    const [lon, lat] = feature.geometry.coordinates;
    field.coordinates = { lat, lon };
    
    if (this.mapComponent) {
      this.mapComponent.setLocationMarker(field.id, field.type, displayName, lat, lon);
      
      this.updateRoute();
    }
    
    this.cdr.markForCheck();
    
    console.log(`Selected ${field.type} location:`, { 
      id: field.id,
      lat, 
      lon, 
      name: displayName 
    });
    this.persistState();
  }

  private clearEstimate(): void {
    this.estimatedDistance = undefined;
    this.estimatedDuration = undefined;
    this.estimatedCost = undefined;
  }

  
  private updateRoute(): void {
    const pickupField = this.pickup;
    const dropoffField = this.dropoff;
    
    if (!pickupField?.coordinates || !dropoffField?.coordinates) {
      this.mapComponent?.clearRoute();
      this.clearEstimate();   
      this.cdr.markForCheck();
      return;
    }
    
    if (this.mapComponent) {
      this.locationFields.forEach(field => {
        if (field.coordinates) {
          this.mapComponent.removeLocationMarker(field.id);
        }
      });
    }
    
    const waypointCoords = this.waypoints
      .filter(w => w.coordinates)
      .map(w => [w.coordinates!.lat, w.coordinates!.lon] as [number, number]);
    
    if (this.mapComponent) {
      this.mapComponent.updateRoute(
        [pickupField.coordinates.lat, pickupField.coordinates.lon],
        [dropoffField.coordinates.lat, dropoffField.coordinates.lon],
        waypointCoords
      );
    }
  }

  get pickup(): LocationField | undefined {
    return this.locationFields.find(f => f.type === 'pickup');
  }

  get waypoints(): LocationField[] {
    return this.locationFields.filter(f => f.type === 'waypoint');
  }

  get dropoff(): LocationField | undefined {
    return this.locationFields.find(f => f.type === 'dropoff');
  }

  getAllLocationsWithCoordinates(): Array<{ lat: number; lon: number; type: string }> {
    return this.locationFields
      .filter(f => f.coordinates)
      .map(f => ({ 
        lat: f.coordinates!.lat, 
        lon: f.coordinates!.lon,
        type: f.type 
      }));
  }

  private getDisplayName(feature: PhotonFeature): string {
    const props = feature.properties;
    let displayName = props.name || '';

    if (props.street) {
      displayName = props.street;
      if (props.housenumber) {
        displayName = `${props.street} ${props.housenumber}`;
      }
    }

    if (!displayName && props.name) {
      displayName = props.name;
    }

    return displayName;
  }

  formatSuggestion(feature: PhotonFeature): string {
    const props = feature.properties;
    let parts: string[] = [];

    if (props.name) parts.push(props.name);
    if (props.street) {
      const street = props.housenumber
        ? `${props.street} ${props.housenumber}`
        : props.street;
      parts.push(street);
    }
    if (props.city && props.city !== props.name) parts.push(props.city);

    return parts.join(', ');
  }
}