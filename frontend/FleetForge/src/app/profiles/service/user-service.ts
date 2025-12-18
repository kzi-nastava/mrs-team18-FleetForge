import { Injectable, signal } from '@angular/core';
import { User } from '../model/user.model';
import { Vehicle } from '../model/vehicle.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private _user=signal<User>(
    {
      id:2,
      firstName:'Milos',
      lastName:'Damjanovic',
      email:'milosdamjanovic@gmail.com',
      number:323432432,
      homeAddress:'Bulevar Oslobodjenja 22',
    }
  )
  user=this._user.asReadonly()

  changeUser(user:User){
    this._user.set(user)
  }

  private _vehicle=signal<Vehicle>(
    {
      id:1,
      model:'Toyota',
      type:'standard',
      licensePlateNumber:'BG1234AB',
      passengerNumber:4,
      isBabySeatAvailable:false,
      isPetFriendly:false
    }
  )
  vehicle=this._vehicle.asReadonly()
  changeVehicle(vehicle:Vehicle){
    this._vehicle.set(vehicle)
  }
}
