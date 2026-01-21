import { ChangeDetectorRef, Component, ViewChild, ViewEncapsulation } from '@angular/core';
import { VehicleLocationDTO, VehicleType } from '../../shared/models/vehicle.model';
import { VehicleService } from '../../shared/services/vehicle.service';
import { MapComponent } from '../../shared/map/map';
import { RouterModule } from '@angular/router';
import { last, map } from 'rxjs';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PassengerHome } from '../service/passenger-home/passenger-home';
import { CommonModule } from '@angular/common';
import { WayPointDTO } from '../../shared/dtos/ride.dtos';

@Component({
  selector: 'app-passenger-home',
  imports: [RouterModule, MapComponent,ReactiveFormsModule,CommonModule],
  templateUrl: './passenger-home.component.html',
  styleUrl: './passenger-home.component.css',
  encapsulation: ViewEncapsulation.None
})
export class PassengerHomeComponent {
  estimatedDistance: number = 0;
  estimatedDuration: number = 0;
  estimatedCost: number = 0;
  passengersNumber: number = 0;
  waypointsNumber: number = 0;
vehicles: VehicleLocationDTO[] = [];

  constructor(private vehicleService: VehicleService, private cdr: ChangeDetectorRef,private passengerHomeService:PassengerHome) {}
  @ViewChild(MapComponent) mapComponent!: MapComponent;
  markers: string[] = [];

  ngOnInit(): void {
   // this.loadVehicles();
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
    this.mapComponent.setMarkerWithCoords(event.address, event.lat, event.lng);
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

 onWaypointChange(event: Event, index: number): void {
  const inputElement = event.target as HTMLInputElement;
  const newValue = inputElement.value;
  
  const oldValue = inputElement.dataset['previousValue'] || '';
  if (oldValue && oldValue.trim() !== '') {
    this.mapComponent.removeMarker(oldValue);
    this.markers = this.markers.filter(marker => marker !== oldValue);
    this.checkAndUpdateRoute();
  }
  inputElement.dataset['previousValue'] = newValue;
  if (index >= 0) {
    this.waypointArray.at(index).setValue(newValue);
  }
  
  if (newValue && newValue.trim() !== '') {
    this.mapComponent.setMarker(newValue).subscribe(() => {
      this.markers.push(newValue);
      this.checkAndUpdateRoute();
    });
  } else {
    this.checkAndUpdateRoute();
  }
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
    datetime: new FormControl('', Validators.required),
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
if(pickupLatLng){
  wayPointsDto.push({
    location: {
      latitude: pickupLatLng.getLatLng().lat,
      longitude: pickupLatLng.getLatLng().lng
    },
    address: this.rideForm.value.pickup!,
    orderIndex: 0
  });
}
      for(let i=0;i<this.waypointArray.length;i++){
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
if(dropoffLatLng){
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
        rideTime: new Date(this.rideForm.value.datetime!).toISOString().slice(0, -1),// Skidanje Z sa kraja stringa za pravilni format
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
          if(response.created){
          console.log('Ride created successfully:', response);
          alert('Ride created successfully!');
          this.rideForm.reset();
          this.waypointArray.clear();
          this.passengerArray.clear();
          this.markers.forEach(marker => this.mapComponent.removeMarker(marker));
          this.mapComponent.clearRoute();
          this.markers = [];
          }else{
            alert('Failed to create ride. There is no free drivers.');
              this.rideForm.reset();
          this.waypointArray.clear();
          this.passengerArray.clear();
          this.markers.forEach(marker => this.mapComponent.removeMarker(marker));
          this.mapComponent.clearRoute();
          this.markers = [];
          }
        },
        error: (error) => {
          console.error('Error creating ride:', error);
          alert('Error creating ride. Please try again.');
        }
      });
    }else{
      alert("Form is not valid!");
    }
  }

  
removeWaypoint(index: number): void {
    const value = this.waypointArray.at(index).value;
    if (value) {
      this.mapComponent.removeMarker(value);
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
}
