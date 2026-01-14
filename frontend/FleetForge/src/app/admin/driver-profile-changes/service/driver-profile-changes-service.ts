import { Injectable, signal } from '@angular/core';
import { DriverProfileChangeRequest } from '../model/driver-profile-change-request';
import { VehicleProfileChangeRequest } from '../model/vehicle-profile-change-request';

@Injectable({
  providedIn: 'root',
})
export class DriverProfileChangesService {
  private _changes=signal<DriverProfileChangeRequest[]>([
    {
      id:1,
      oldFristName: 'John',
      newFirstName: 'Jonathan',
      oldLastName: 'Doe',
      newLastName: 'Doe',
      oldEmail: 'john.doe@example.com',
      newEmail: 'jonathan.doe@example.com',
      oldPhoneNumber: '123-456-7890',
      newPhoneNumber: '098-765-4321',
      oldAddress: '123 Main St',
      newAddress: '456 Elm St',
      oldProfilePictureUrl: 'blank_profile.webp',
      newProfilePictureUrl: 'blank_profile.webp'
    },{
      id:2,
      oldFristName: 'Jane',
      newFirstName: 'Janet',
      oldLastName: 'Smith',
      newLastName: 'Smith',
      oldEmail: 'jane.smith@example.com',
      newEmail: 'janet.smith@example.com',
      oldPhoneNumber: '234-567-8901',
      newPhoneNumber: '210-987-6543',
      oldAddress: '789 Oak St',
      newAddress: '321 Pine St',
      oldProfilePictureUrl: 'blank_profile.webp',
      newProfilePictureUrl: 'blank_profile.webp'
    }
  ])
  changes=this._changes.asReadonly();

  private _vehicleChanges=signal<VehicleProfileChangeRequest[]>([
    {
      id:1,
      firstName: 'John',
      lastName: 'Doe',
      oldModel: 'Toyota Camry',
      newModel: 'Honda Accord',
      oldType:"VAN",
      newType:"LUXURY",
      oldRegistrationNumber: "VS-243-FG",
      newRegistrationNumber: "LUX-876-RT",
      oldSpace: 4,
      newSpace: 5,
      oldBabySeat: false,
      newBabySeat: true,
      oldPetFriendly: false,
      newPetFriendly: true
    },{
      id:2,
      firstName: 'Jane',
      lastName: 'Smith',
      oldModel: 'Ford Focus',
      newModel: 'Chevrolet Malibu',
      oldType:"STANDARD",
      newType:"STANDARD",
      oldRegistrationNumber: "AB-123-CD",
      newRegistrationNumber: "EF-456-GH",
      oldSpace: 4,
      newSpace: 4,
      oldBabySeat: true,
      newBabySeat: false,
      oldPetFriendly: true,
      newPetFriendly: false
    }
  ]);
  vehicleChanges=this._vehicleChanges.asReadonly();


}
