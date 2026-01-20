import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MapComponent } from '../shared/map/map';
import { VehicleService } from '../shared/services/vehicle.service';
import { VehicleLocationDTO } from '../shared/models/vehicle.model';
import { PhotonFeature, PhotonService } from '../shared/services/photon.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

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

  pickupQuery = '';
  pickupSuggestions: PhotonFeature[] = [];
  showPickupSuggestions = false;

  waypointQuery = '';
  waypointSuggestions: PhotonFeature[] = [];
  showWaypointSuggestions = false;

  dropoffQuery = '';
  dropoffSuggestions: PhotonFeature[] = [];
  showDropoffSuggestions = false;

  private pickupSearchSubject = new Subject<string>();
  private waypointSearchSubject = new Subject<string>();
  private dropoffSearchSubject = new Subject<string>();

  constructor(
    private vehicleService: VehicleService,
    private photonService: PhotonService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadVehicles();
    this.setupPickupSearchStream();
    this.setupWaypointSearchStream();
    this.setupDropoffSearchStream();
  }

  ngOnDestroy(): void {
    this.pickupSearchSubject.complete();
    this.waypointSearchSubject.complete();
    this.dropoffSearchSubject.complete();
  }

  private setupPickupSearchStream(): void {
    this.pickupSearchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((query: string) => {
        if (query.length < 2) {
          this.pickupSuggestions = [];
          this.showPickupSuggestions = false;
          this.cdr.markForCheck();
          return [];
        }
        return this.photonService.searchSuggestions(query);
      })
    ).subscribe({
      next: (features: PhotonFeature[]) => {
        this.pickupSuggestions = features;
        this.showPickupSuggestions = features.length > 0;
        this.cdr.markForCheck();
      },
      error: (error: any) => {
        console.error('Error fetching pickup suggestions:', error);
        this.pickupSuggestions = [];
        this.showPickupSuggestions = false;
        this.cdr.markForCheck();
      }
    });
  }

  private setupWaypointSearchStream(): void {
    this.waypointSearchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((query: string) => {
        if (query.length < 2) {
          this.waypointSuggestions = [];
          this.showWaypointSuggestions = false;
          this.cdr.markForCheck();
          return [];
        }
        return this.photonService.searchSuggestions(query);
      })
    ).subscribe({
      next: (features: PhotonFeature[]) => {
        this.waypointSuggestions = features;
        this.showWaypointSuggestions = features.length > 0;
        this.cdr.markForCheck();
      },
      error: (error: any) => {
        console.error('Error fetching waypoint suggestions:', error);
        this.waypointSuggestions = [];
        this.showWaypointSuggestions = false;
        this.cdr.markForCheck();
      }
    });
  }

  private setupDropoffSearchStream(): void {
    this.dropoffSearchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((query: string) => {
        if (query.length < 2) {
          this.dropoffSuggestions = [];
          this.showDropoffSuggestions = false;
          this.cdr.markForCheck();
          return [];
        }
        return this.photonService.searchSuggestions(query);
      })
    ).subscribe({
      next: (features: PhotonFeature[]) => {
        this.dropoffSuggestions = features;
        this.showDropoffSuggestions = features.length > 0;
        this.cdr.markForCheck();
      },
      error: (error: any) => {
        console.error('Error fetching dropoff suggestions:', error);
        this.dropoffSuggestions = [];
        this.showDropoffSuggestions = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadVehicles(): void {
    this.vehicleService.getActiveVehicles().subscribe({
      next: (vehicles) => {
        this.vehicles = [...vehicles]; 
        this.cdr.markForCheck();
        console.log('Loaded vehicles:', this.vehicles);
        console.log('vehicles property after assignment:', this.vehicles);
      },
      error: (error) => {
        console.error('Error loading vehicles:', error);
      }
    });
  }

  onPickupInput(): void {
    this.pickupSearchSubject.next(this.pickupQuery);
  }

  onWaypointInput(): void {
    this.waypointSearchSubject.next(this.waypointQuery);
  }

  onDropoffInput(): void {
    this.dropoffSearchSubject.next(this.dropoffQuery);
  }

  selectPickup(feature: PhotonFeature): void {
    const displayName = this.getDisplayName(feature);
    this.pickupQuery = displayName;
    this.pickupSuggestions = [];
    this.showPickupSuggestions = false;
    this.cdr.markForCheck();

    const [lon, lat] = feature.geometry.coordinates;
    console.log('Selected pickup location:', { lat, lon, name: displayName });
  }

  selectWaypoint(feature: PhotonFeature): void {
    const displayName = this.getDisplayName(feature);
    this.waypointQuery = displayName;
    this.waypointSuggestions = [];
    this.showWaypointSuggestions = false;
    this.cdr.markForCheck();

    const [lon, lat] = feature.geometry.coordinates;
    console.log('Selected waypoint location:', { lat, lon, name: displayName });
  }

  selectDropoff(feature: PhotonFeature): void {
    const displayName = this.getDisplayName(feature);
    this.dropoffQuery = displayName;
    this.dropoffSuggestions = [];
    this.showDropoffSuggestions = false;
    this.cdr.markForCheck();

    const [lon, lat] = feature.geometry.coordinates;
    console.log('Selected dropoff location:', { lat, lon, name: displayName });
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
