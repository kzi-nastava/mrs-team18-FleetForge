import { Component, ElementRef, Signal, ViewChild, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { User } from '../model/user.model';
import { UserService } from '../service/user-service';
import { Vehicle } from '../model/vehicle.model';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-user-profile-driver',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  RouterLink],
  templateUrl: './user-profile-driver.component.html',
  styleUrl: './user-profile-driver.component.css',
  encapsulation: ViewEncapsulation.None
})
export class UserProfileDriverComponent {
editDriver(): void {

    if (this.editDriverInfo.invalid) return;
    const user: User = {
       id: Math.random(),
      name: this.editDriverInfo.value.name ?? '',
      email: this.editDriverInfo.value.email ?? '',
      number: Number(this.editDriverInfo.value.number) || 0,
      age: Number(this.editDriverInfo.value.age) || 0
    };
    this.userService.changeUser(user);
}
editVehicle(): void {
  if(this.editVehicleInfo.invalid) return;

  const vehicle: Vehicle={
    id: Math.random(),
    carName: this.editVehicleInfo.value.carName ?? '',
    engineNumber: this.editVehicleInfo.value.engineNumber ?? '',
    licensePlateNumber: this.editVehicleInfo.value.licensePlateNumber ?? '',
    passengerNumber: Number(this.editVehicleInfo.value.passengerNumber) || 0
  };
  this.userService.changeVehicle(vehicle);
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
  }

  editDriverInfo=new FormGroup({
    name: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    number: new FormControl('',Validators.required),
    age: new FormControl('',Validators.required)
  });
  protected userShow: Signal<User>;
  protected vehicleShow: Signal<Vehicle>;
  constructor(private userService: UserService) {
    this.userShow=this.userService.user
    this.vehicleShow=this.userService.vehicle
  }
ngOnInit(): void {
    const currentUser = this.userShow();
    this.editDriverInfo.patchValue({
      name: currentUser.name,
      email: currentUser.email,
      number: currentUser.number.toString(),
      age: currentUser.age.toString()
    });
    const currentVehicle = this.vehicleShow();
    this.editVehicleInfo.patchValue({
      carName: currentVehicle.carName,
      engineNumber: currentVehicle.engineNumber,
      licensePlateNumber: currentVehicle.licensePlateNumber,
      passengerNumber: currentVehicle.passengerNumber.toString()
    });
  }
  editVehicleInfo=new FormGroup({
    carName: new FormControl('', Validators.required),
    engineNumber: new FormControl('', Validators.required),
    licensePlateNumber: new FormControl('',Validators.required),
    passengerNumber: new FormControl('',Validators.required)
  });
  // protected vehicleShow: Signal<User>;
  // constructor(private vehicleService: UserService) {
  //   this.vehicleShow=this.vehicleService.user
  // }
}
