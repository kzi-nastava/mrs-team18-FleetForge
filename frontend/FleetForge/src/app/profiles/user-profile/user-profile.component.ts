import { ChangeDetectionStrategy, Component, ElementRef, inject, OnInit, Signal, ViewChild, ViewEncapsulation } from '@angular/core';

import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { User } from '../model/user.model';
import { UserService } from '../service/user-service';
import { MatDialog, MatDialogActions, MatDialogClose, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';


@Component({
  selector: 'app-user-profile',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterLink],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css',
  encapsulation: ViewEncapsulation.None
})
export class UserProfileComponent implements OnInit {
  editUserInfo=new FormGroup({
    name: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    number: new FormControl('',Validators.required),
    age: new FormControl('',Validators.required)
  });
  protected userShow: Signal<User>;
  constructor(private userService: UserService) {
    this.userShow=this.userService.user
  }

  ngOnInit(): void {
    const currentUser = this.userShow();
    this.editUserInfo.patchValue({
      name: currentUser.name,
      email: currentUser.email,
      number: currentUser.number.toString(),
      age: currentUser.age.toString()
    });
  }

edit(): void {
    if (this.editUserInfo.invalid) return;

    const user: User = {
       id: Math.random(),
      name: this.editUserInfo.value.name ?? '',
      email: this.editUserInfo.value.email ?? '',
      number: Number(this.editUserInfo.value.number) || 0,
      age: Number(this.editUserInfo.value.age) || 0
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
