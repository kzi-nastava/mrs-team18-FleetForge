import { CommonModule } from '@angular/common';
import { Component, ElementRef, model, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { form } from '@angular/forms/signals';

@Component({
  selector: 'app-register-driver',
  imports: [CommonModule,ReactiveFormsModule],
  templateUrl: './register-driver.component.html',
  styleUrl: './register-driver.component.css',
})
export class RegisterDriverComponent {
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
  imageUrl: string = 'blank_profile.webp';
  openFilePicker(): void {
    this.fileInput.nativeElement.click();
  }
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    // ovde treba sacuvati sliku na server ili u bazu podataka
    // za sada samo prikazujemo izabranu sliku
    // pozivom servisa this.userService.uploadProfilePicture(file);
  this.imageUrl = URL.createObjectURL(file);
  this.registerDriverForm.patchValue({
    driverInfo: {
      profilePicture: file.name
    }
  });
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

    console.log('Form Data:', this.registerDriverForm.value);
    
   
alert('Driver successfully registered!');
  this.resetForm(); 
}

// Nova metoda za reset
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
