import { Component, ElementRef, OnInit, Signal, ViewChild, ViewEncapsulation } from '@angular/core';

import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { User } from '../model/user.model';
import { UserService } from '../service/user-service';


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
    number: new FormControl('',Validators.required),
    address: new FormControl('',Validators.required)
  });
  protected userShow: Signal<User>;
  constructor(private userService: UserService) {
    this.userShow=this.userService.user
  }

  ngOnInit(): void {
    const currentUser = this.userShow();
    this.editPassengerInfo.patchValue({
      firstName: currentUser.firstName,
      lastName: currentUser.lastName,
      email: currentUser.email,
      number: currentUser.number.toString(),
      address: currentUser.homeAddress
    });
  }

edit(): void {
    if (this.editPassengerInfo.invalid) return;

    const user: User = {
       id: Math.random(),
      firstName: this.editPassengerInfo.value.firstName ?? '',
      lastName: this.editPassengerInfo.value.lastName ?? '',
      email: this.editPassengerInfo.value.email ?? '',
      number: Number(this.editPassengerInfo.value.number) || 0,
      homeAddress: this.editPassengerInfo.value.address ?? ''
    };

    this.userService.changeUser(user);
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
}
