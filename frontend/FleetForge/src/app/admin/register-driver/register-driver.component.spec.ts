import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Driver } from "../../shared/models/user.model";
import { RegisterDriverComponent } from './register-driver.component';
import { VehicleType } from '../../shared/models/vehicle.model';
import { DriverCreateResponseDTO } from '../../shared/dtos/driver.dtos';
import { RegisterDriverService } from '../service/register-driver/register-driver.service';
import { of } from 'rxjs';
function fillValidDriverInfo(component: RegisterDriverComponent): void {
  component.driverInfoFormGroup.setValue({
    firstName: 'Marko',
    lastName: 'Marković',
    email: 'marko@gmail.com',
    phoneNumber: '+381601234567',
    address: 'Spens, Novi Sad',
    profilePicture: 'blank_profile.webp',
  });
}
function fillValidVehicleInfo(component: RegisterDriverComponent): void {
  component.vehicleInfo.setValue({
    model: 'Toyota Corolla',
    type: VehicleType.STANDARD,
    registrationNumber: 'NS-123-AB',
    space: 4,
    babySeat: true,
    petFriendly: false,
  });
}

describe('RegisterDriverComponent', () => {
  let component: RegisterDriverComponent;
  let fixture: ComponentFixture<RegisterDriverComponent>;
  const serviceSpy:DriverCreateResponseDTO={
    driverId: 1
  }

  beforeEach(async () => {
    spyOn(window, 'alert');
    const registerDriverService=jasmine.createSpyObj('RegisterDriverService', ['registerDriver', 'uploadProfilePicture']);
    await TestBed.configureTestingModule({
      
      imports: [RegisterDriverComponent],
      providers:[
        {provide: RegisterDriverService, useValue: registerDriverService}
      ] 
    })
    .compileComponents();
    
     
     registerDriverService.registerDriver.and.returnValue(of(serviceSpy));
     registerDriverService.uploadProfilePicture.and.returnValue(of({}));

    fixture = TestBed.createComponent(RegisterDriverComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  describe('Form init', () => {
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start with driver form', () => {
    expect(component.showVehicleForm).toBeFalse();
  });
  it('should load default profile picture', () => {
    expect(component.imageUrl).toBe('blank_profile.webp');
  });
  it('should initialise all driverInfo controls with empty strings', () => {
      const di = component.driverInfoFormGroup;
      expect(di.get('firstName')?.value).toBe('');
      expect(di.get('lastName')?.value).toBe('');
      expect(di.get('email')?.value).toBe('');
      expect(di.get('phoneNumber')?.value).toBe('');
      expect(di.get('address')?.value).toBe('');
    });
    it('should initialise boolean vehicleInfo controls with false', () => {
      expect(component.vehicleInfo.get('babySeat')?.value).toBe(false);
      expect(component.vehicleInfo.get('petFriendly')?.value).toBe(false);
    });
});
  describe('Picture upload', () => {
    it('should update imageUrl on file selection', () => {
      const file = new File([''], 'test-image.jpg', { type: 'image/*' });
      const event = { target: { files: [file] } };
      spyOn(URL, 'createObjectURL').and.returnValue('blob:http://localhost/test-image');
      component.onFileSelected(event as any);
      expect(component.imageUrl).toBe('blob:http://localhost/test-image');
      expect(component.formData.get('file')).toBe(file);
      expect(component.registerDriverForm.get('driverInfo')?.get('profilePicture')?.value).toBe('test-image.jpg');
    });
    it('should do nothing when no file is present in the event', () => {
      const originalUrl = component.imageUrl;
      const fakeEvent = {target: { files: [] },}
      component.onFileSelected(fakeEvent as any);
      expect(component.imageUrl).toBe(originalUrl);
    });
  });

  describe('Next button', () => {
    it('should NOT advance to vehicle step when driverInfo is invalid', () => {
      component.goToVehicleForm();
      expect(component.showVehicleForm).toBeFalse();
    });
    describe('driverInfo form group-validation', () => {
      it('should mark first name invalid and touche it', () => {
        const field = component.driverInfoFormGroup.get('firstName')!;
        field.setValue('');
        component.goToVehicleForm();
        expect(field.invalid).toBeTrue();
        expect(field.touched).toBeTrue();
      });
      it('should mark last name invalid and touche it', () => {
        const field = component.driverInfoFormGroup.get('lastName')!;
        field.setValue('');
        component.goToVehicleForm();
        expect(field.invalid).toBeTrue();
        expect(field.touched).toBeTrue();
      });
        it('should mark email invalid and touche it', () => {
        const field = component.driverInfoFormGroup.get('email')!;
        field.setValue('invalid-email');
        component.goToVehicleForm();
        expect(field.invalid).toBeTrue();
        expect(field.touched).toBeTrue();
      });
        it('should mark phone number invalid and touche it', () => {
        const field = component.driverInfoFormGroup.get('phoneNumber')!;
        field.setValue('');
        component.goToVehicleForm();
        expect(field.invalid).toBeTrue();
        expect(field.touched).toBeTrue();
      });
        it('should mark address invalid and touche it', () => {
        const field = component.driverInfoFormGroup.get('address')!;
        field.setValue('');
        component.goToVehicleForm();
        expect(field.invalid).toBeTrue();
        expect(field.touched).toBeTrue();
      });
  });
   it('should advance to vehicle step when driverInfo is valid', () => {
      fillValidDriverInfo(component);
      component.goToVehicleForm();
      expect(component.showVehicleForm).toBeTrue();
    });
  });
  describe('Back button', () => {
    it('should return to the driver-info step', () => {
      component.showVehicleForm = true;
      component.goToDriverForm();
      expect(component.showVehicleForm).toBeFalse();
    });
  });

  describe('Register button', () => {
      it('should NOT submit when vehicleInfo is invalid', () => {
        component.showVehicleForm = true;
        component.registerDriver();
        expect(component.vehicleInfo.invalid).toBeTrue();
      });
      describe('vehicleInfo form group-validation', () => {
        it('should mark model invalid and touche it', () => {
        const field = component.vehicleInfo.get('model')!;
        field.setValue('');
        component.registerDriver();
        expect(field.invalid).toBeTrue();
        expect(field.touched).toBeTrue();
      });
      it('should mark type invalid and touche it', () => {
        const field = component.vehicleInfo.get('type')!;
        field.setValue('');
        component.registerDriver();
        expect(field.invalid).toBeTrue();
        expect(field.touched).toBeTrue();
      });
        it('should mark registration number invalid and touche it', () => {
        const field = component.vehicleInfo.get('registrationNumber')!;
        field.setValue('');
        component.registerDriver();
        expect(field.invalid).toBeTrue();
        expect(field.touched).toBeTrue();
      });
        it('should mark space invalid and touche it', () => {
        const field = component.vehicleInfo.get('space')!;
        field.setValue('');
        component.registerDriver();
        expect(field.invalid).toBeTrue();
        expect(field.touched).toBeTrue();
        });
      });
  
    });
    describe('Successful registration without profile picture', () => {
    it('should submit valid form and reset it', () => {
      fillValidDriverInfo(component);
      fillValidVehicleInfo(component);
      component.registerDriver();
      const service = fixture.debugElement.injector.get(RegisterDriverService)as jasmine.SpyObj<RegisterDriverService>;
      expect(service.registerDriver).toHaveBeenCalled()
      const returnedObservable = service.registerDriver.calls.mostRecent().returnValue;
      returnedObservable.subscribe((response: DriverCreateResponseDTO) => {
          expect(response.driverId).toBe(1);
      });
      expect(service.registerDriver).toHaveBeenCalledWith({
        firstName: 'Marko',
        lastName: 'Marković',
        email: 'marko@gmail.com',
        phoneNumber: '+381601234567',
        address: 'Spens, Novi Sad',
        vehicle: {
          model: 'Toyota Corolla',
          type: VehicleType.STANDARD,
          registrationNumber: 'NS-123-AB',
          space: 4,
          babySeat: true,
          petFriendly: false,
        }
      });
      expect(service.uploadProfilePicture).not.toHaveBeenCalled();
      expect(component.registerDriverForm.untouched).toBeTrue();
      expect(component.registerDriverForm.get('driverInfo')?.get('firstName')?.value).toBe('');
      expect(component.registerDriverForm.get('driverInfo')?.get('lastName')?.value).toBe('');
      expect(component.registerDriverForm.get('driverInfo')?.get('email')?.value).toBe('');
      expect(component.registerDriverForm.get('driverInfo')?.get('phoneNumber')?.value).toBe('');
      expect(component.registerDriverForm.get('driverInfo')?.get('address')?.value).toBe('');
      expect(component.registerDriverForm.get('vehicleInfo')?.get('model')?.value).toBe('');
      expect(component.registerDriverForm.get('vehicleInfo')?.get('type')?.value).toBe('');
      expect(component.registerDriverForm.get('vehicleInfo')?.get('registrationNumber')?.value).toBe('');
      expect(component.registerDriverForm.get('vehicleInfo')?.get('space')?.value).toBe('');
      expect(component.registerDriverForm.get('vehicleInfo')?.get('babySeat')?.value).toBe(false);
      expect(component.registerDriverForm.get('vehicleInfo')?.get('petFriendly')?.value).toBe(false);
      expect(component.imageUrl).toBe('blank_profile.webp');
    });
    it('should show a success alert after a successful registration', () => {
      fillValidDriverInfo(component);
      fillValidVehicleInfo(component);
      component.registerDriver();
      expect(window.alert).toHaveBeenCalledWith('Driver successfully registered!');
    });
  });
    describe('Successful registration with profile picture', () => {
    it('should submit valid form with profile picture and reset it', () => {
      const file = new File([''], 'test-image.jpg', { type: 'image/*' });
      const event = { target: { files: [file] } };
      component.onFileSelected(event as any);
      expect(component.imageUrl).toContain('blob:');
      fillValidDriverInfo(component);
      fillValidVehicleInfo(component);
      component.registerDriverForm.get('driverInfo')?.get('profilePicture')?.setValue(file.name);
      component.registerDriver();
      const service = fixture.debugElement.injector.get(RegisterDriverService)as jasmine.SpyObj<RegisterDriverService>;
      expect(service.registerDriver).toHaveBeenCalled()
      const returnedObservable = service.registerDriver.calls.mostRecent().returnValue;
      returnedObservable.subscribe((response: DriverCreateResponseDTO) => {
          expect(response.driverId).toBe(1);
      });
      expect(service.registerDriver).toHaveBeenCalledWith({
        firstName: 'Marko',
        lastName: 'Marković',
        email: 'marko@gmail.com',
        phoneNumber: '+381601234567',
        address: 'Spens, Novi Sad',
        vehicle: {
          model: 'Toyota Corolla',
          type: VehicleType.STANDARD,
          registrationNumber: 'NS-123-AB',
          space: 4,
          babySeat: true,
          petFriendly: false,
        }
        
      });
      expect(service.uploadProfilePicture).toHaveBeenCalledWith(component.formData, 1);
      expect(component.registerDriverForm.untouched).toBeTrue();
      expect(component.registerDriverForm.get('driverInfo')?.get('firstName')?.value).toBe('');
      expect(component.registerDriverForm.get('driverInfo')?.get('lastName')?.value).toBe('');
      expect(component.registerDriverForm.get('driverInfo')?.get('email')?.value).toBe('');
      expect(component.registerDriverForm.get('driverInfo')?.get('phoneNumber')?.value).toBe('');
      expect(component.registerDriverForm.get('driverInfo')?.get('address')?.value).toBe('');
      expect(component.registerDriverForm.get('vehicleInfo')?.get('model')?.value).toBe('');
      expect(component.registerDriverForm.get('vehicleInfo')?.get('type')?.value).toBe('');
      expect(component.registerDriverForm.get('vehicleInfo')?.get('registrationNumber')?.value).toBe('');
      expect(component.registerDriverForm.get('vehicleInfo')?.get('space')?.value).toBe('');
      expect(component.registerDriverForm.get('vehicleInfo')?.get('babySeat')?.value).toBe(false);
      expect(component.registerDriverForm.get('vehicleInfo')?.get('petFriendly')?.value).toBe(false);
      expect(component.imageUrl).toBe('blank_profile.webp');

    });  
    it('should show a success alert after a successful registration', () => {
      fillValidDriverInfo(component);
      fillValidVehicleInfo(component);
      component.registerDriver();
      expect(window.alert).toHaveBeenCalledWith('Driver successfully registered!');
    });
  });
});

