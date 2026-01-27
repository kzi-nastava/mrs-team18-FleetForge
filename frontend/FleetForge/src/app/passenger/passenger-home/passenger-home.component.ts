import { ChangeDetectorRef, Component, OnDestroy, ViewChild, ViewEncapsulation } from '@angular/core';
import { VehicleLocationDTO, VehicleType } from '../../shared/models/vehicle.model';
import { VehicleService } from '../../shared/services/vehicle.service';
import { MapComponent } from '../../shared/map/map';
import { RouterModule } from '@angular/router';
import { debounceTime, distinctUntilChanged, forkJoin, last, map, Subject, Subscription, switchMap } from 'rxjs';
import * as L from 'leaflet';
import {
  AbstractControl,
  FormArray,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { PassengerHome } from '../service/passenger-home/passenger-home';
import { CommonModule } from '@angular/common';
import { WayPointDTO } from '../../shared/dtos/ride.dtos';
import { PhotonFeature, PhotonService } from '../../shared/services/photon.service';
import { RideService } from '../../shared/services/ride.service';

interface LocationSuggestions {
  pickup: PhotonFeature[];
  dropoff: PhotonFeature[];
  waypoints: Map<number, PhotonFeature[]>;
}

interface SuggestionsVisibility {
  pickup: boolean;
  dropoff: boolean;
  waypoints: Map<number, boolean>;
}

@Component({
  selector: 'app-passenger-home',
  imports: [RouterModule, MapComponent,ReactiveFormsModule,CommonModule],
  templateUrl: './passenger-home.component.html',
  styleUrl: './passenger-home.component.css',
  encapsulation: ViewEncapsulation.None
})
export class PassengerHomeComponent implements OnDestroy {
  suggestions: LocationSuggestions = {
    pickup: [],
    dropoff: [],
    waypoints: new Map()
  };

  showSuggestions: SuggestionsVisibility = {
    pickup: false,
    dropoff: false,
    waypoints: new Map()
  };
  private pickupSearchSubject = new Subject<string>();
  private dropoffSearchSubject = new Subject<string>();
  private waypointSearchSubjects = new Map<number, Subject<string>>();
  
  private subscriptions: Subscription[] = [];
  estimatedDistance: number = 0;
  estimatedDuration: number = 0;
  estimatedCost: number = 0;
  passengersNumber: number = 0;
  waypointsNumber: number = 0;
vehicles: VehicleLocationDTO[] = [];

  minDateTime: string = '';

  constructor(public rideService: RideService, private cdr: ChangeDetectorRef,private passengerHomeService:PassengerHome,private photonService:PhotonService) {}
  @ViewChild(MapComponent) mapComponent!: MapComponent;
  markers: string[] = [];

  private minDateTimeValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null; 
      }

      const selectedDate = new Date(control.value);
      const minDate = new Date();
      minDate.setMinutes(minDate.getMinutes() + 60);
      minDate.setSeconds(0, 0);

      if (selectedDate < minDate) {
        return { 
          minDateTime: { 
            requiredMin: this.minDateTime, 
            actual: control.value 
          } 
        };
      }

      return null;
    };
  }

  ngOnInit(): void {
     // this.loadVehicles();
     this.updateMinDateTime();
    setInterval(() => this.updateMinDateTime(), 60000);
    this.setupPickupSearch();
    this.setupDropoffSearch();
  }
   ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.pickupSearchSubject.complete();
    this.dropoffSearchSubject.complete();
    this.waypointSearchSubjects.forEach(subject => subject.complete());
  }

   private setupPickupSearch(): void {
    const subscription = this.pickupSearchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((query: string) => {
        if (query.length < 2) {
          this.suggestions.pickup = [];
          this.showSuggestions.pickup = false;
          return [];
        }
        return this.photonService.searchSuggestions(query);
      })
    ).subscribe({
      next: (features: PhotonFeature[]) => {
        this.suggestions.pickup = features;
        this.showSuggestions.pickup = features.length > 0;
        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Error fetching pickup suggestions:', error);
        this.suggestions.pickup = [];
        this.showSuggestions.pickup = false;
      }
    });

    this.subscriptions.push(subscription);
  }

  private setupDropoffSearch(): void {
    const subscription = this.dropoffSearchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((query: string) => {
        if (query.length < 2) {
          this.suggestions.dropoff = [];
          this.showSuggestions.dropoff = false;
          return [];
        }
        return this.photonService.searchSuggestions(query);
      })
    ).subscribe({
      next: (features: PhotonFeature[]) => {
        this.suggestions.dropoff = features;
        this.showSuggestions.dropoff = features.length > 0;
        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Error fetching dropoff suggestions:', error);
        this.suggestions.dropoff = [];
        this.showSuggestions.dropoff = false;
      }
    });

    this.subscriptions.push(subscription);
  }

  private setupWaypointSearch(index: number): void {
    const subject = new Subject<string>();
    this.waypointSearchSubjects.set(index, subject);

    const subscription = subject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((query: string) => {
        if (query.length < 2) {
          this.suggestions.waypoints.set(index, []);
          this.showSuggestions.waypoints.set(index, false);
          return [];
        }
        return this.photonService.searchSuggestions(query);
      })
    ).subscribe({
      next: (features: PhotonFeature[]) => {
        this.suggestions.waypoints.set(index, features);
        this.showSuggestions.waypoints.set(index, features.length > 0);
        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error(`Error fetching waypoint ${index} suggestions:`, error);
        this.suggestions.waypoints.set(index, []);
        this.showSuggestions.waypoints.set(index, false);
      }
    });

    this.subscriptions.push(subscription);
  }

   onPickupInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const query = input.value;

    if (!query || query.trim().length === 0) {
      const oldValue = this.rideForm.get('pickup')?.value;
      if (this.mapComponent) {
        this.mapComponent.removeLocationMarker('pickup');
        if (oldValue) {
           this.markers = this.markers.filter(m => m !== oldValue);
           this.mapComponent['locationMarkers'].delete(oldValue);
        }
      }

      this.rideForm.get('pickup')?.setValue('');
      this.suggestions.pickup = [];
      this.showSuggestions.pickup = false;
      this.checkAndUpdateRoute();
      return;
    }

    this.pickupSearchSubject.next(query);
  }

  onDropoffInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const query = input.value;

    if (!query || query.trim().length === 0) {
       const oldValue = this.rideForm.get('dropoff')?.value;
      if (this.mapComponent) {
        this.mapComponent.removeLocationMarker('dropoff');
        if (oldValue) {
          this.markers = this.markers.filter(m => m !== oldValue);
          this.mapComponent['locationMarkers'].delete(oldValue);
        }
    }
    
    this.rideForm.get('dropoff')?.setValue('');
    this.suggestions.dropoff = [];
    this.showSuggestions.dropoff = false;
    this.checkAndUpdateRoute();
      return;
    }

    this.dropoffSearchSubject.next(query);
  }

  onWaypointInput(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const query = input.value;

    if (!this.waypointSearchSubjects.has(index)) {
      this.setupWaypointSearch(index);
    }

    if (!query || query.trim().length === 0) {
      const oldValue = this.waypointArray.at(index).value;
    const waypointId = `waypoint-${index}`;
    
    if (oldValue && this.mapComponent) {
      this.mapComponent.removeLocationMarker(waypointId);
      this.markers = this.markers.filter(m => m !== oldValue);
      this.mapComponent['locationMarkers'].delete(oldValue);
    }
    
    this.waypointArray.at(index).setValue('');
    this.suggestions.waypoints.set(index, []);
    this.showSuggestions.waypoints.set(index, false);
    this.checkAndUpdateRoute();
    return;
    }

    this.waypointSearchSubjects.get(index)!.next(query);
  }
    selectPickupSuggestion(feature: PhotonFeature): void {
    const displayName = this.getDisplayName(feature);
    const oldValue = this.rideForm.get('pickup')?.value;

    // Ukloni stari marker
    if (oldValue && this.mapComponent) {
      this.mapComponent.removeLocationMarker('pickup');
      this.markers = this.markers.filter(m => m !== oldValue);
    }

    this.rideForm.get('pickup')?.setValue(displayName);
    this.suggestions.pickup = [];
    this.showSuggestions.pickup = false;

    const [lon, lat] = feature.geometry.coordinates;

    if (this.mapComponent) {
      this.mapComponent.setLocationMarker('pickup', 'pickup', displayName, lat, lon);
    }

    const marker = L.marker([lat, lon]);
    this.mapComponent['locationMarkers'].set(displayName, marker);

    if (!this.markers.includes(displayName)) {
      this.markers.push(displayName);
    }

    this.checkAndUpdateRoute();
    this.cdr.markForCheck();
  }

  selectDropoffSuggestion(feature: PhotonFeature): void {
    const displayName = this.getDisplayName(feature);
    const oldValue = this.rideForm.get('dropoff')?.value;

    // Ukloni stari marker
    if (oldValue && this.mapComponent) {
      this.mapComponent.removeLocationMarker('dropoff');
      this.markers = this.markers.filter(m => m !== oldValue);
    }

    this.rideForm.get('dropoff')?.setValue(displayName);
    this.suggestions.dropoff = [];
    this.showSuggestions.dropoff = false;

    const [lon, lat] = feature.geometry.coordinates;

    if (this.mapComponent) {
      this.mapComponent.setLocationMarker('dropoff', 'dropoff', displayName, lat, lon);
    }

    const marker = L.marker([lat, lon]);
    this.mapComponent['locationMarkers'].set(displayName, marker);

    if (!this.markers.includes(displayName)) {
      this.markers.push(displayName);
    }

    this.checkAndUpdateRoute();
    this.cdr.markForCheck();
  }

  selectWaypointSuggestion(feature: PhotonFeature, index: number): void {
    const displayName = this.getDisplayName(feature);
    const oldValue = this.waypointArray.at(index).value;
    const waypointId = `waypoint-${index}`;

    // Ukloni stari marker
    if (oldValue && this.mapComponent) {
      this.mapComponent.removeLocationMarker(waypointId);
      this.markers = this.markers.filter(m => m !== oldValue);
    }

    this.waypointArray.at(index).setValue(displayName);
    this.suggestions.waypoints.set(index, []);
    this.showSuggestions.waypoints.set(index, false);

    const [lon, lat] = feature.geometry.coordinates;

    if (this.mapComponent) {
      this.mapComponent.setLocationMarker(waypointId, 'waypoint', displayName, lat, lon);
    }

    const marker = L.marker([lat, lon]);
    this.mapComponent['locationMarkers'].set(displayName, marker);

    if (!this.markers.includes(displayName)) {
      this.markers.push(displayName);
    }

    this.checkAndUpdateRoute();
    this.cdr.markForCheck();
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
  ngAfterViewInit(): void {
    const data = history.state.favoriteRoute;
    if (data) {
      this.rideForm.get('pickup')?.setValue(data.startAddress);
      this.rideForm.get('dropoff')?.setValue(data.endAddress);

      const addresses = [
        data.startAddress,
        data.endAddress,
        ...data.waypoints.map((wp: WayPointDTO) => wp.address)
      ];

      const geocodeObservables = addresses.map(addr =>
        this.photonService.searchSuggestions(addr)
      );

      forkJoin(geocodeObservables).subscribe({
        next: (results) => {
          results.forEach((features, index) => {
            if (features && features.length > 0) {
              const [lon, lat] = features[0].geometry.coordinates;
              const marker = L.marker([lat, lon]);
              const address = addresses[index];

              this.mapComponent['locationMarkers'].set(address, marker);
              this.markers.push(address);
            }
          });

          data.waypoints.forEach((wp: WayPointDTO) => {
            const newControl = new FormControl(wp.address, { validators: Validators.required, nonNullable: true });
            this.waypointArray.push(newControl);
          });

          this.waypointsNumber = this.waypointArray.length;
          this.checkAndUpdateRoute();
          this.cdr.detectChanges();
        },
        error: (error:any) => {
          console.error('Geocoding error:', error);
        }
      });
    }
  }
  private updateMinDateTime(): void {
   const now = new Date();
  now.setMinutes(now.getMinutes() + 60); 
  now.setSeconds(0, 0);

  const pad = (n: number) => n.toString().padStart(2, '0');

  this.minDateTime =
    now.getFullYear() + '-' +
    pad(now.getMonth() + 1) + '-' +
    pad(now.getDate()) + 'T' +
    pad(now.getHours()) + ':' +
    pad(now.getMinutes());
  }

  scrollToBookRide(): void {
    document.getElementById('bookRide')?.scrollIntoView();
  }

  // loadVehicles(): void {
  //   this.vehicleService.getActiveVehicles().subscribe({
  //     next: (vehicles) => {
  //       this.vehicles = [...vehicles]; 
  //       this.cdr.detectChanges(); 
  //       console.log('Loaded vehicles:', this.vehicles);
  //       console.log('vehicles property after assignment:', this.vehicles);
  //     },
  //     error: (error) => {
  //       console.error('Error loading vehicles:', error);
  //     }
  //   });
  // }
  addPassengerInput(): void {
  this.passengerArray.push(
      new FormControl('', { validators: [Validators.required, Validators.email], nonNullable: true })
    );
  }

  addWaypointInput(): void {
    this.waypointArray.push(
      new FormControl('', { validators: Validators.required, nonNullable: true })
    );
   }

  handleMapClick(event: {address:string, lat:number, lng:number}): void {
    this.markers.push(event.address);
    const marker = L.marker([event.lat, event.lng]);
    this.mapComponent['locationMarkers'].set(event.address, marker);
    const newControl = new FormControl(event.address, { validators: Validators.required, nonNullable: true });
  this.waypointArray.push(newControl);
  this.waypointsNumber = this.waypointArray.length;
    Promise.resolve().then(() => {
    const inputs = document.querySelectorAll('.waypoint-input-wrapper input') as NodeListOf<HTMLInputElement>;
    const lastInput = inputs[inputs.length - 2];
    if (lastInput) {
      lastInput.dataset['previousValue'] = event.address;
    }
    this.checkAndUpdateRoute();
    this.cdr.detectChanges();
  });
  }


private checkAndUpdateRoute(): void {
  const startingPoint = document.getElementById('pickup') as HTMLInputElement;
  const endingPoint = document.getElementById('dropoff') as HTMLInputElement;
  
  const waypoints: [number, number][] = [];
this.waypointArray.controls.forEach(control => {
    const addr = control.value;
    if (addr && addr.trim() !== '') {
      const marker = this.mapComponent['locationMarkers'].get(addr);
      if (marker) {
        const latLng = marker.getLatLng();
        waypoints.push([latLng.lat, latLng.lng]);
      }
    }
  });
  const pickupMarker = this.mapComponent['locationMarkers'].get(startingPoint.value);
  const dropoffMarker = this.mapComponent['locationMarkers'].get(endingPoint.value);
  
  if (pickupMarker && dropoffMarker) {
    const pickupLatLng = pickupMarker.getLatLng();
    const dropoffLatLng = dropoffMarker.getLatLng();
    this.mapComponent.updateRoute(
      [pickupLatLng.lat, pickupLatLng.lng],
      [dropoffLatLng.lat, dropoffLatLng.lng],
      waypoints
    );
  } else {
    this.mapComponent.clearRoute();
  }
}
 

   onRouteSummary(summary: { distanceKm: number; durationMin: number; cost: number }): void {
    this.estimatedDistance = summary.distanceKm;
    this.estimatedDuration = summary.durationMin;
    this.estimatedCost = summary.cost;
    console.log('Route summary received in component:', summary);
  }
  private refactorIdsWaypoints(): void {
  const container = document.getElementsByClassName('newWaypoints')[0] as HTMLElement;
  if (!container) return;
  
  const waypointInputs = container.querySelectorAll('input[id^="waypoint"]');
  waypointInputs.forEach((input: Element, index: number) => {
    const waypointInput = input as HTMLInputElement;
    const newId = 'waypoint' + (index + 1);
    waypointInput.id = newId;
    
    const label = container.querySelector(`label[for^="waypoint"]`);
    if (label) {
      label.setAttribute('for', newId);
      label.textContent = `Waypoint ${index + 1}:`;
    }
  });
  
  this.waypointsNumber = waypointInputs.length;
}

rideForm = new FormGroup({
    pickup: new FormControl('', Validators.required),
    dropoff: new FormControl('', Validators.required),
  datetime: new FormControl('', [
      Validators.required,
      this.minDateTimeValidator() 
    ]),
    passengers: new FormControl(0, [Validators.required, Validators.min(1)]),
    now: new FormControl(false),
    waypoint: new FormArray<FormControl<string>>([]),
    passenger: new FormArray<FormControl<string>>([]),
    type: new FormControl('', Validators.required),
    babySeat: new FormControl(false),
    petFriendly: new FormControl(false),
  });
get waypointArray(): FormArray<FormControl<string>> {
  return this.rideForm.get('waypoint') as FormArray<FormControl<string>>;
}
get passengerArray(): FormArray<FormControl<string>> {
  return this.rideForm.get('passenger') as FormArray<FormControl<string>>;
}
onSubmit(): void {
  console.log(this.rideForm.value);
  if (this.rideForm.valid) {
    const wayPointsDto: WayPointDTO[] = [];
    const pickupLatLng = this.mapComponent['locationMarkers'].get(this.rideForm.value.pickup!);
    if (pickupLatLng) {
      wayPointsDto.push({
        location: {
          latitude: pickupLatLng.getLatLng().lat,
          longitude: pickupLatLng.getLatLng().lng
        },
        address: this.rideForm.value.pickup!,
        orderIndex: 0
      });
    }
    
    for (let i = 0; i < this.waypointArray.length; i++) {
      wayPointsDto.push({
        location: {
          latitude: this.mapComponent['locationMarkers'].get(this.waypointArray.at(i).value)?.getLatLng().lat || 0,
          longitude: this.mapComponent['locationMarkers'].get(this.waypointArray.at(i).value)?.getLatLng().lng || 0
        },
        address: this.waypointArray.at(i).value,
        orderIndex: i
      });
    }
    
    const dropoffLatLng = this.mapComponent['locationMarkers'].get(this.rideForm.value.dropoff!);
    if (dropoffLatLng) {
      wayPointsDto.push({
        location: {
          latitude: dropoffLatLng.getLatLng().lat,
          longitude: dropoffLatLng.getLatLng().lng
        },
        address: this.rideForm.value.dropoff!,
        orderIndex: wayPointsDto.length
      });
    }
    
    const rideRequest = {
      coordinates: wayPointsDto,
      passengerNumber: this.rideForm.value.passengers || 0,
      rideTime: this.rideForm.value.now ? new Date().toISOString().slice(0, -1) : new Date(this.rideForm.value.datetime!).toISOString().slice(0, -1),
      rideNow: this.rideForm.value.now || false,
      passengerEmails: this.passengerArray.value || [],
      vehicleType: this.rideForm.value.type as VehicleType || VehicleType.STANDARD,
      babySeat: this.rideForm.value.babySeat || false,
      petFriendly: this.rideForm.value.petFriendly || false,
      startAddress: this.rideForm.value.pickup || '',
      endAddress: this.rideForm.value.dropoff || '',
      totalDistance: this.estimatedDistance || 0,
      estimatedDuration: this.estimatedDuration || 0
    };
    
    console.log('Ride Request:', rideRequest);
    this.passengerHomeService.createRide(rideRequest).subscribe({
      next: (response) => {
        if (response.created) {
          console.log('Ride created successfully:', response);
          alert('Ride created successfully!');
        } else {
          alert('Failed to create ride. There is no free drivers.');
        }
        
        this.mapComponent.removeLocationMarker('pickup');
        this.mapComponent.removeLocationMarker('dropoff');
        
        for (let i = 0; i < this.waypointArray.length; i++) {
          this.mapComponent.removeLocationMarker(`waypoint-${i}`);
        }
        
        this.markers.forEach(marker => {
          this.mapComponent.removeMarker(marker);
          this.mapComponent['locationMarkers'].delete(marker);
        });
        
        this.mapComponent.clearRoute();
        
        this.rideForm.reset();
        this.waypointArray.clear();
        this.passengerArray.clear();
        this.markers = [];
        this.rideForm.get('datetime')?.enable();
        this.rideForm.get('passengers')?.setValue(0);
        
        this.suggestions.pickup = [];
        this.suggestions.dropoff = [];
        this.suggestions.waypoints.clear();
        this.showSuggestions.pickup = false;
        this.showSuggestions.dropoff = false;
        this.showSuggestions.waypoints.clear();
        
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error creating ride:', error);
        alert('Error creating ride. Please try again.');
      }
    });
  } else {
    alert("Form is not valid!");
  }
}
  
removeWaypoint(index: number): void {
  const value = this.waypointArray.at(index).value;
  const waypointId = `waypoint-${index}`;
  
  if (value) {
    this.mapComponent.removeLocationMarker(waypointId);
    this.mapComponent.removeMarker(value);
    this.markers = this.markers.filter(m => m !== value);
    this.mapComponent['locationMarkers'].delete(value);
  }
  
  this.waypointArray.removeAt(index);
  this.waypointsNumber--;
  this.refactorIdsWaypoints();
  this.checkAndUpdateRoute();
}
  removePassenger(index: number): void {
    this.passengerArray.removeAt(index);
    this.passengersNumber--;
  }

  onNowToggle(): void {
    if (this.rideForm.value.now) {
      this.rideForm.get('datetime')?.disable();
    } else {
      this.rideForm.get('datetime')?.enable();
    }
  }
}
