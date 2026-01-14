import { Component, ElementRef, OnInit, Signal, ViewChild, ViewEncapsulation } from '@angular/core';

import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { User } from '../../shared/models/user.model';
import { PassengerService } from '../service/passenger-service';


@Component({
  selector: 'app-user-profile',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterLink],
  templateUrl: './passenger-profile.component.html',
  styleUrl: './passenger-profile.component.css',
  encapsulation: ViewEncapsulation.None
})
export class PassengerProfileComponent implements OnInit {
  editPassengerInfo=new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    phoneNumber: new FormControl('',Validators.required),
    address: new FormControl('',Validators.required),
    profilePicture: new FormControl('')
  });
  // protected userShow: Signal<User>;
  constructor(private passengerService:PassengerService,) {
    // this.userShow=this.userService.user
  }

  ngOnInit(): void {
     this.passengerService.getCurrentPassenger().subscribe((userData) => {
       this.editPassengerInfo.setValue({
      firstName: userData.firstName ?? '',
      lastName: userData.lastName ?? '',
      email: userData.email ?? '',
      phoneNumber: userData.phoneNumber ?? '', 
      address: userData.address ?? '',
      profilePicture: userData.profilePicture ?? ''
    });
    });
  }

edit(): void {
    if (this.editPassengerInfo.invalid) return;
    this.passengerService.changePassenger({
      firstName: this.editPassengerInfo.value.firstName ?? '',
      lastName: this.editPassengerInfo.value.lastName ?? '',
      email: this.editPassengerInfo.value.email ?? '',
      phoneNumber: this.editPassengerInfo.value.phoneNumber ?? '',
      address: this.editPassengerInfo.value.address ?? '',
      profilePicture: this.editPassengerInfo.value.profilePicture ?? 'blank_profile.webp'
    })
    .subscribe((updatedUser) => {
      alert('Profile successfully updated!');
      this.editPassengerInfo.setValue({
        firstName: updatedUser.firstName ?? '',
        lastName: updatedUser.lastName ?? '',
        email: updatedUser.email ?? '',
        phoneNumber: updatedUser.phoneNumber ?? '',
        address: updatedUser.address ?? '',
        profilePicture: updatedUser.profilePicture ?? 'blank_profile.webp'
      });
    });
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
    this.editPassengerInfo.patchValue({
    profilePicture: file.name 
  });
  }
}
