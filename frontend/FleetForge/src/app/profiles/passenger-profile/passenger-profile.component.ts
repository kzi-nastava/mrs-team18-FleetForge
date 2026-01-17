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
  formData = new FormData();
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
      profilePicture: "http://localhost:8080" + (userData.profilePicture ?? 'blank_profile.webp')
    });
    this.imageUrl="http://localhost:8080"+ (userData.profilePicture ?? 'blank_profile.webp');
    
    });
  }

edit(): void {
    if (this.editPassengerInfo.invalid) return;
    this.passengerService.changePassenger({
      firstName: this.editPassengerInfo.value.firstName ?? '',
      lastName: this.editPassengerInfo.value.lastName ?? '',
      email: this.editPassengerInfo.value.email ?? '',
      phoneNumber: this.editPassengerInfo.value.phoneNumber ?? '',
      address: this.editPassengerInfo.value.address ?? ''
    })
    .subscribe((updatedUser) => {
      if(this.formData.has('file')) {
        this.passengerService.uploadProfilePicture(this.formData).subscribe(); 
      }
      alert('Passenger information updated successfully.');
    });
  }


   

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
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
    this.imageUrl = URL.createObjectURL(file);
    this.formData.set('file', file);
    this.editPassengerInfo.patchValue({
    profilePicture: file.name 
  });
  }
}
