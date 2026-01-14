import { Component, OnInit, Signal, signal, WritableSignal } from '@angular/core';
import { DriverProfileChangePendingRequestsDTO, VehicleProfileChangePendingRequestsDTO } from '../../shared/dtos/driver-profile-requests.dtos';
import { DriverProfileChangesService } from '../service/driver-profile-changes-service';

@Component({
  selector: 'app-driver-profile-changes',
  imports: [],
  templateUrl: './driver-profile-changes.component.html',
  styleUrl: './driver-profile-changes.component.css',
})
export class DriverProfileChangesComponent  implements OnInit {
  protected expandedIds = new Set<number>();
  protected isSliderRight = false;
  
protected changes: WritableSignal<DriverProfileChangePendingRequestsDTO[]> = signal([]);
protected vehicleChanges: WritableSignal<VehicleProfileChangePendingRequestsDTO[]> = signal([]);
  
  constructor(private service :DriverProfileChangesService){
    
  }
  ngOnInit(): void {
    this.service.findAllPendingRequests().subscribe(data => {
      this.changes.set(data);
      
    });
    this.service.findAllVehiclePendingRequests().subscribe(data => {
      this.vehicleChanges.set(data);
    });
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

  approveDriver(requestId:number,driverId:number):void{
    this.service.sendDriverChangeDecision({change:true},requestId).subscribe(response=>{
      alert('Driver change request approved.');
      this.ngOnInit();
    });
  }

  rejectDriver(requestId:number,driverId:number):void{
    this.service.sendDriverChangeDecision({change:false},requestId).subscribe(response=>{
      alert('Driver change request rejected.');
      this.ngOnInit();
    });
  }
  approveVehicle(requestId:number,vehicleId:number):void{
    this.service.sendVehicleChangeDecision({change:true},requestId).subscribe(response=>{
      alert('Vehicle change request approved.');
      this.ngOnInit();
    });
  }
  rejectVehicle(requestId:number,vehicleId:number):void{
    this.service.sendVehicleChangeDecision({change:false},requestId).subscribe(response=>{
      alert('Vehicle change request rejected.');
      this.ngOnInit();
    });
  }
}
