import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../service/admin-service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-profile',
  imports: [ReactiveFormsModule,RouterModule],
  templateUrl: './admin-profile.component.html',
  styleUrl: './admin-profile.component.css',
})
export class AdminProfileComponent implements OnInit {
  adminId: number = 0;
  
  editAdminInfo=new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    phoneNumber: new FormControl('',Validators.required),
    address: new FormControl('',Validators.required),
    profilePicture: new FormControl('')
  });

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.adminService.getCurrentAdmin().subscribe((adminData) => {
      this.editAdminInfo.setValue({
        firstName: adminData.firstName ?? '',
        lastName: adminData.lastName ?? '',
        email: adminData.email ?? '',
        phoneNumber: adminData.phoneNumber ?? '',
        address: adminData.address ?? '',
        profilePicture: adminData.profilePicture ?? ''
      });
    });
  }

  edit(): void {
    if (this.editAdminInfo.invalid) return;
    this.adminService.changeAdmin({
      firstName: this.editAdminInfo.value.firstName ?? '',
      lastName: this.editAdminInfo.value.lastName ?? '',
      email: this.editAdminInfo.value.email ?? '',
      phoneNumber: this.editAdminInfo.value.phoneNumber ?? '',
      address: this.editAdminInfo.value.address ?? '',
      profilePicture: this.editAdminInfo.value.profilePicture ?? 'blank_profile.webp'
    })
    .subscribe((updatedAdmin) => {
      alert('Profile successfully updated!');
      this.editAdminInfo.setValue({
        firstName: updatedAdmin.firstName ?? '',
        lastName: updatedAdmin.lastName ?? '',
        email: updatedAdmin.email ?? '',
        phoneNumber: updatedAdmin.phoneNumber ?? '',
        address: updatedAdmin.address ?? '',
        profilePicture: updatedAdmin.profilePicture ?? 'blank_profile.webp'
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
    this.editAdminInfo.patchValue({
    profilePicture: file.name 
  } );
  }
}