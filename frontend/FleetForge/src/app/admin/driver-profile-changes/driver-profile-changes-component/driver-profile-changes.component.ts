import { Component, Signal } from '@angular/core';
import { DriverProfileChangesService } from '../service/driver-profile-changes-service';
import { DriverProfileChangeRequest } from '../model/driver-profile-change-request';
import { VehicleProfileChangeRequest } from '../model/vehicle-profile-change-request';

@Component({
  selector: 'app-driver-profile-changes',
  imports: [],
  templateUrl: './driver-profile-changes.component.html',
  styleUrl: './driver-profile-changes.component.css',
})
export class DriverProfileChangesComponent {
  protected changes:Signal<DriverProfileChangeRequest[]>;
  protected expandedIds = new Set<number>();
  protected isSliderRight = false;
  protected vehicleChanges:Signal<VehicleProfileChangeRequest[]>;
  
  constructor(private service:DriverProfileChangesService){
    this.changes=this.service.changes;
    this.vehicleChanges=this.service.vehicleChanges;
  }

  toggleDetails(changeId: number): void {
    const newSet = new Set(this.expandedIds); 
    if (newSet.has(changeId)) {
      newSet.delete(changeId);
    } else {
      newSet.add(changeId);
    }
    this.expandedIds = newSet; 
  }

  isExpanded(changeId: number): boolean {
    return this.expandedIds.has(changeId);
  }

  toggleSlider(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    this.isSliderRight = checkbox.checked;
  }
}
