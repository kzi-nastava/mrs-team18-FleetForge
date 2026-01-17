import { CommonModule } from '@angular/common';
import { Component, ElementRef, model, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { form } from '@angular/forms/signals';

import { DriverCreateRequestDTO } from '../../shared/dtos/driver.dtos';
import { RegisterDriverService } from '../service/register-driver/register-driver.service';
@Component({
  selector: 'app-register-driver',
  imports: [CommonModule,ReactiveFormsModule],
  templateUrl: './register-driver.component.html',
  styleUrl: './register-driver.component.css',
})
export class RegisterDriverComponent {
  constructor(private registerDriverService: RegisterDriverService) {}
  
  showVehicleForm: boolean = false;

  goToVehicleForm(): void {
    if(!this.registerDriverForm.get('driverInfo')?.valid) {
      this.registerDriverForm.get('driverInfo')?.markAllAsTouched();
      return;
    } 
    this.showVehicleForm = true;
  }
  goToDriverForm(): void {
    this.showVehicleForm = false;
  }
@ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  formData = new FormData();
  imageUrl: string = 'blank_profile.webp';
  openFilePicker(): void {
    this.fileInput.nativeElement.click();
  }
  onFileSelected(event: Event): void {
    if(this.formData.has('file')){
      this.formData.delete('file');
    }
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.formData.append('file', file);
     this.imageUrl = URL.createObjectURL(file);
    this.registerDriverForm.patchValue({
    driverInfo: {
      profilePicture: file.name 
    }
  } );
  }
registerDriverForm = new FormGroup({
  driverInfo: new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),
    phoneNumber: new FormControl('', Validators.required),
    address: new FormControl('', Validators.required),
    profilePicture: new FormControl('blank_profile.webp'),
  }),
  vehicleInfo: new FormGroup({
    model: new FormControl('', Validators.required),
    type: new FormControl('', Validators.required),
    registrationNumber: new FormControl('', Validators.required),
    space: new FormControl('', Validators.required),
    babySeat: new FormControl(false),
    petFriendly: new FormControl(false)
  })
});


registerDriver(): void {
   if (this.vehicleInfo.invalid) {
      this.vehicleInfo.markAllAsTouched();
      return;
    }

    if (this.registerDriverForm.invalid) {
      this.registerDriverForm.markAllAsTouched();
      return;
    }
    const request: DriverCreateRequestDTO = { 
  firstName: this.driverInfoFormGroup.get('firstName')?.value!,
  lastName: this.driverInfoFormGroup.get('lastName')?.value!,
  email: this.driverInfoFormGroup.get('email')?.value!,
  phoneNumber: this.driverInfoFormGroup.get('phoneNumber')?.value!,
  address: this.driverInfoFormGroup.get('address')?.value!,
  vehicle: {
    model: this.vehicleInfo.get('model')?.value!,
    type: this.vehicleInfo.get('type')?.value!,
    registrationNumber: this.vehicleInfo.get('registrationNumber')?.value!,
    space: this.vehicleInfo.get('space')?.value!,
    babySeat: this.vehicleInfo.get('babySeat')?.value!,
    petFriendly: this.vehicleInfo.get('petFriendly')?.value!
  }
    };

  this.registerDriverService.registerDriver(request).subscribe({
      next: (response) => {
        this.registerDriverService.uploadProfilePicture(this.formData, response.driver.id).subscribe();
        alert('Driver successfully registered!');
      },  
      error: (error) => {
        console.error('Error registering driver:', error);
      }
    });
    
   

  this.resetForm(); 
}

resetForm(): void {
  this.registerDriverForm.reset({
    driverInfo: {
      firstName: '',
      lastName: '',
      email: '',
      phoneNumber: '',
      address: '',
      profilePicture: 'blank_profile.webp'
    },
    vehicleInfo: {
      model: '',
      type: '',
      registrationNumber: '',
      space: '',
      babySeat: false,
      petFriendly: false
    }
  });
  
  this.showVehicleForm = false;
  
  this.imageUrl = 'blank_profile.webp';
}
get driverInfoFormGroup(): FormGroup {
  return this.registerDriverForm.get('driverInfo') as FormGroup;
}

get vehicleInfo(): FormGroup {
  return this.registerDriverForm.get('vehicleInfo') as FormGroup;
}
}
