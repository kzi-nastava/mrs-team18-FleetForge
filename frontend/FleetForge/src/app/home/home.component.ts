import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MapComponent } from '../shared/map/map';
import { VehicleService } from '../shared/services/vehicle.service';
import { VehicleLocationDTO } from '../shared/models/vehicle.model';
import { PhotonFeature, PhotonService } from '../shared/services/photon.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

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
  vehicles: VehicleLocationDTO[] = [];
  
  // Location fields array
  locationFields: LocationField[] = [];
  
  private subscriptions: Subscription[] = [];
  private nextWaypointId = 0;

  constructor(
    private vehicleService: VehicleService,
    private photonService: PhotonService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadVehicles();
    this.initializeLocationFields();
  }

  ngOnDestroy(): void {
    // Clean up all subscriptions and subjects
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.locationFields.forEach(field => field.searchSubject.complete());
  }

  private initializeLocationFields(): void {
    // Initialize with pickup and dropoff
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
    
    // Insert waypoints before dropoff
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

  // Add a new waypoint
  addWaypoint(): void {
    this.addLocationField('waypoint');
    this.cdr.markForCheck();
  }

  // Remove a waypoint by ID
  removeWaypoint(waypointId: string): void {
    const index = this.locationFields.findIndex(f => f.id === waypointId);
    if (index !== -1) {
      const field = this.locationFields[index];
      field.searchSubject.complete();
      this.locationFields.splice(index, 1);
      this.cdr.markForCheck();
    }
  }

  // Handle input changes
  onLocationInput(field: LocationField): void {
    field.searchSubject.next(field.query);
  }

  // Handle suggestion selection
  selectSuggestion(field: LocationField, feature: PhotonFeature): void {
    const displayName = this.getDisplayName(feature);
    field.query = displayName;
    field.suggestions = [];
    field.showSuggestions = false;
    
    const [lon, lat] = feature.geometry.coordinates;
    field.coordinates = { lat, lon };
    
    this.cdr.markForCheck();
    
    console.log(`Selected ${field.type} location:`, { 
      id: field.id,
      lat, 
      lon, 
      name: displayName 
    });
  }

  // Get pickup location
  get pickup(): LocationField | undefined {
    return this.locationFields.find(f => f.type === 'pickup');
  }

  // Get all waypoints
  get waypoints(): LocationField[] {
    return this.locationFields.filter(f => f.type === 'waypoint');
  }

  // Get dropoff location
  get dropoff(): LocationField | undefined {
    return this.locationFields.find(f => f.type === 'dropoff');
  }

  // Get all locations with coordinates (for routing)
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