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
  formData = new FormData();
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
        profilePicture: "http://localhost:8080" + (adminData.profilePicture ?? 'blank_profile.webp')
      });
      this.imageUrl="http://localhost:8080"+ (adminData.profilePicture ?? 'blank_profile.webp');
    });
  }

  edit(): void {
    if (this.editAdminInfo.invalid) return;
    this.adminService.changeAdmin({
      firstName: this.editAdminInfo.value.firstName ?? '',
      lastName: this.editAdminInfo.value.lastName ?? '',
      email: this.editAdminInfo.value.email ?? '',
      phoneNumber: this.editAdminInfo.value.phoneNumber ?? '',
      address: this.editAdminInfo.value.address ?? ''
    })
    .subscribe((updatedAdmin) => {
      if(this.formData.has('file')) {
        this.adminService.uploadProfilePicture(this.formData).subscribe(); 
      }
      alert('Admin information updated successfully.');
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
    this.editAdminInfo.patchValue({
    profilePicture: file.name 
  } );
  }
}