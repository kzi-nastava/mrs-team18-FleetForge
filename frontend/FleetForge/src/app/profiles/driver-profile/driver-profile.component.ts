import { ChangeDetectorRef, Component, ElementRef, signal, Signal, ViewChild, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { User } from '../../shared/models/user.model';
import { Vehicle } from '../model/vehicle.model';
import { RouterLink } from '@angular/router';
import { DriverService } from '../service/driver-service';
import { VehicleType } from '../../shared/models/vehicle.model';
import { form } from '@angular/forms/signals';
import { interval, Subscription, switchMap } from 'rxjs';

@Component({
  selector: 'app-driver-profile',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  RouterLink],
  templateUrl: './driver-profile.component.html',
  styleUrl: './driver-profile.component.css',
  encapsulation: ViewEncapsulation.None
})
export class DriverProfileComponent {
  activity: number = 0;
  formData = new FormData();
  driverEmail: string = '';
  constructor(private driverService: DriverService,private cdr: ChangeDetectorRef) { }
  isBlocked = signal(false);
  blockReason = signal('');
  showBlockedDialog = signal(false);

openBlockedDialog(): void {
  this.showBlockedDialog.set(true);
}

closeBlockedDialog(): void {
  this.showBlockedDialog.set(false);
}

editDriver(): void {
  if (this.isBlocked()) {
    return;
  }

  const file = this.formData.get('file') as File | null;
  const fileExtension = file ? file.name.substring(file.name.lastIndexOf('.')) : '';
  console.log("img: " + this.driverEmail + fileExtension);
  this.driverService.createChangeRequest({
    newFirstName: this.editDriverInfo.value.firstName ?? '',
    newLastName: this.editDriverInfo.value.lastName ?? '',
    newEmail: this.editDriverInfo.value.email ?? '',
    newPhoneNumber: this.editDriverInfo.value.phoneNumber ?? '',
    newAddress: this.editDriverInfo.value.address ?? ''
  }).subscribe((response) =>  {
    alert('Profile change request submitted for approval.');
  });
}
editVehicle(): void {
  if (this.isBlocked()) {
    return;
  }

  this.driverService.createVehicleChangeRequest({
    newModel: this.editVehicleInfo.value.model ?? '',
    newType: (this.editVehicleInfo.value.type ?? '') as VehicleType,
    newRegistrationNumber: this.editVehicleInfo.value.registrationNumber ?? '',
    newSpace: Number(this.editVehicleInfo.value.space) ?? 0,
    newBabySeat: this.editVehicleInfo.value.babySeat ?? false,
    newPetFriendly: this.editVehicleInfo.value.petFriendly ?? false
  }).subscribe((response) =>  {
  alert('Vehicle change request submitted for approval.');}
  );
}

@ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  imageUrl: string = 'blank_profile.webp';
  openFilePicker(): void {
    if (this.isBlocked()) {
      return;
    }

    this.fileInput.nativeElement.click();
  }
  onFileSelected(event: Event): void {
    if(this.formData.has('file')){
      this.formData.delete('file');
    }
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.formData.set('file', file);
    this.imageUrl = URL.createObjectURL(file);
    this.editDriverInfo.patchValue({
    profilePicture: file.name 
  } );
  this.driverService.uploadProfilePicture(this.formData).subscribe();
  }

  editDriverInfo=new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    phoneNumber: new FormControl('',Validators.required),
    address : new FormControl('',Validators.required),
    profilePicture: new FormControl('')
  });
   editVehicleInfo=new FormGroup({
    model: new FormControl('', Validators.required),
    type: new FormControl('', Validators.required),
    registrationNumber: new FormControl('',Validators.required),
    space: new FormControl('',[Validators.required,Validators.min(1)]),
    babySeat: new FormControl(false),
    petFriendly: new FormControl(false)
  });
ngOnInit(): void {
    this.driverService.getIsBlocked().subscribe({
      next: (response) => {
        this.isBlocked.set(response.blocked);
        this.blockReason.set(response.reason || 'No reason provided.');
        if (response.blocked) {
          this.openBlockedDialog();
        }
      },
      error: () => {
        this.isBlocked.set(false);
        this.blockReason.set('');
      }
    });

    this.driverService.getCurrentDriver().subscribe((driverData) => {
      this.driverEmail=driverData.email ?? '';
       this.editDriverInfo.setValue({
      firstName: driverData.firstName ?? '',
      lastName: driverData.lastName ?? '',
      email: driverData.email ?? '',
      phoneNumber: driverData.phoneNumber ?? '', 
      address: driverData.address ?? '',
      profilePicture: "http://localhost:8080" + (driverData.profilePicture ?? 'blank_profile.webp')
  
      
    });

    this.editVehicleInfo.setValue({
      model: driverData.vehicle.model ?? '',
      type: driverData.vehicle.type ?? '',
      registrationNumber: driverData.vehicle.registrationNumber ?? '',
      space: (driverData.vehicle.space ?? '').toString(),
      babySeat: driverData.vehicle.babySeat ?? false,
      petFriendly: driverData.vehicle.petFriendly ?? false
    });
    this.imageUrl="http://localhost:8080"+ (driverData.profilePicture ?? 'blank_profile.webp');
    this.driverService.getDriverActivty().subscribe(activityData => {
    this.activity = activityData.activeSecondsLast24h ?? 0;
    this.cdr.detectChanges();
  });

   
  } 
  
);
}
    get formattedActivity(): string {
    const totalSeconds = Math.floor(this.activity);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;
    
    return `${hours}h ${minutes}m ${seconds}s`;
  }

}
