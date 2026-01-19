import { ChangeDetectorRef, Component, ViewChild, ViewEncapsulation } from '@angular/core';
import { VehicleLocationDTO } from '../../shared/models/vehicle.model';
import { VehicleService } from '../../shared/services/vehicle.service';
import { MapComponent } from '../../shared/map/map';
import { RouterModule } from '@angular/router';
import { map } from 'rxjs';

@Component({
  selector: 'app-passenger-home',
  imports: [RouterModule, MapComponent],
  templateUrl: './passenger-home.component.html',
  styleUrl: './passenger-home.component.css',
  encapsulation: ViewEncapsulation.None
})
export class PassengerHomeComponent {
  passengersNumber: number = 0;
  waypointsNumber: number = 0;
vehicles: VehicleLocationDTO[] = [];

  constructor(private vehicleService: VehicleService, private cdr: ChangeDetectorRef) {}
  @ViewChild(MapComponent) mapComponent!: MapComponent;
  markers: L.Marker[] = [];

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
    const container = document.getElementsByClassName('newPassengers')[0] as HTMLElement;
    if (container) {
      const label = document.createElement('label');
      label.innerText = `Passenger ${this.passengersNumber + 1}:`;
      label.setAttribute('for', 'passenger' + (this.passengersNumber + 1));

      const inputGroup = document.createElement('div');
      inputGroup.className = 'passenger-input-wrapper';

      const input = document.createElement('input');
      input.id = 'passenger' + (this.passengersNumber + 1); 
      input.setAttribute('formControlName', 'passenger' + (this.passengersNumber + 1));
      input.type = 'text';
      input.placeholder = 'Passenger Email';
      inputGroup.prepend(input);
      input.addEventListener('change', (event) => this.onPassengerChange(event));

      const removeButton = document.createElement('button');
      removeButton.type = 'button';
      removeButton.className = 'btn-add-passenger';
      removeButton.innerText = '-';
      removeButton.onclick = () => {
        container.removeChild(inputGroup);
        container.removeChild(label);
        this.passengersNumber--;
      };

     const element=document.getElementById('newPassengerAdd') as HTMLElement;
     container.insertBefore(inputGroup, element);
     container.insertBefore(label, inputGroup);
      inputGroup.appendChild(removeButton);
      this.passengersNumber++;
    } 
  }

  addWaypointInput(): void {
    const container = document.getElementsByClassName('newWaypoints')[0] as HTMLElement;
    if (container) {
      const label = document.createElement('label');
      label.innerText = `Waypoint ${this.waypointsNumber + 1}:`;
      label.setAttribute('for', 'waypoint' + (this.waypointsNumber + 1));
      const inputGroup = document.createElement('div');
      inputGroup.className = 'waypoint-input-wrapper';

      const input = document.createElement('input');
      input.dataset['previousValue'] = '';
      input.addEventListener('change', (event) => this.onWaypointChange(event));
      input.id = 'waypoint' + (this.waypointsNumber + 1); 
      input.setAttribute('formControlName', 'waypoint' + (this.waypointsNumber + 1));
      input.type = 'text';
      input.placeholder = 'New Waypoint';
      inputGroup.prepend(input);
      const removeButton = document.createElement('button');
      removeButton.type = 'button';
      removeButton.className = 'btn-add-waypoint';
      removeButton.innerText = '-';
      removeButton.onclick = () => {
        container.removeChild(inputGroup);
        container.removeChild(label);
        this.mapComponent.removeMarker(input.value);
        this.waypointsNumber--;
      };
      const element=document.getElementById('waypointAdd') as HTMLElement;
      container.insertBefore(inputGroup, element);
      container.insertBefore(label, inputGroup);
      inputGroup.appendChild(removeButton);
      this.waypointsNumber++;
    }
  }

  handleMapClick(event: {address:string}): void {
    this.mapComponent.setMarker(event.address);
    document.getElementById("addWaypoint")?.click();
    const waypointElement = document.getElementById('waypoint' + this.waypointsNumber) as HTMLInputElement;
    if (waypointElement) {
      waypointElement.value = event.address;
      waypointElement['dataset']['previousValue'] = event.address;
    }
    
    this.cdr.detectChanges();
  }

  onWaypointChange(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    const newValue = inputElement.value;
    
    const oldValue = inputElement.dataset['previousValue'] || '';
    if (oldValue && oldValue.trim() !== '') {
        this.mapComponent.removeMarker(oldValue);
    }
    if (newValue && newValue.trim() !== '') {
        this.mapComponent.setMarker(newValue);
    }
    inputElement.dataset['previousValue'] = newValue;
  }
  onPassengerChange(event: Event): void {
    
  }
}
