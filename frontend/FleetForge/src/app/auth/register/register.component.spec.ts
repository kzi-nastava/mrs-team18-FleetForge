import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { RegisterComponent } from './register.component';
import { AuthService } from '../service/auth.service';
import { NotificationPopupComponent } from '../../shared/popups/popup-dialog/notification-popup.component';
import { ChangeDetectorRef } from '@angular/core';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;
  let changeDetectorRef: jasmine.SpyObj<ChangeDetectorRef>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj('AuthService', [
      'register',
      'checkEmailAvailability'
    ]);

    router = jasmine.createSpyObj('Router', ['navigate']);

    changeDetectorRef = jasmine.createSpyObj('ChangeDetectorRef', ['detectChanges']);

    await TestBed.configureTestingModule({
      imports: [FormsModule, RegisterComponent, NotificationPopupComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
        { provide: ChangeDetectorRef, useValue: changeDetectorRef }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // INIT
  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty form fields', () => {
    // Arrange (handled in beforeEach)

    // Act
    const {
      firstName,
      lastName,
      email,
      phone,
      password,
      confirmPassword,
      address,
      selectedProfilePicture
    } = component;

    // Assert
    expect(firstName).toBe('');
    expect(lastName).toBe('');
    expect(email).toBe('');
    expect(phone).toBe('');
    expect(password).toBe('');
    expect(confirmPassword).toBe('');
    expect(address).toBe('');
    expect(selectedProfilePicture).toBeNull();
  });

  it('should initialize with submitted as false', () => {
    // Arrange (handled in beforeEach)

    // Act
    const submitted = component.submitted;

    // Assert
    expect(submitted).toBeFalse();
  });

  it('should clear localStorage on init', () => {
    // Arrange
    const spy = spyOn(Storage.prototype, 'removeItem');

    // Act
    const newFixture = TestBed.createComponent(RegisterComponent);
    newFixture.detectChanges();

    // Assert
    expect(spy).toHaveBeenCalledWith('token');
    expect(spy).toHaveBeenCalledWith('role');
  });

  // FORM VALIDATION
  it('should mark form as invalid when required fields are empty', () => {
    // Arrange
    const form: any = {
      invalid: true,
      controls: {
        firstName: { markAsTouched: jasmine.createSpy() },
        lastName: { markAsTouched: jasmine.createSpy() },
        email: { markAsTouched: jasmine.createSpy() }
      }
    };

    // Act
    component.register(form);

    // Assert
    expect(component.submitted).toBeTrue();
    expect(form.controls.firstName.markAsTouched).toHaveBeenCalled();
    expect(form.controls.lastName.markAsTouched).toHaveBeenCalled();
    expect(form.controls.email.markAsTouched).toHaveBeenCalled();
  });

  it('should not submit when passwords do not match', () => {
    // Arrange
    component.firstName = 'John';
    component.lastName = 'Doe';
    component.email = 'john@example.com';
    component.phone = '+123456789';
    component.password = 'password123';
    component.confirmPassword = 'password456';
    component.address = '123 Main St';

    // Act
    component.register({ invalid: false, controls: {} } as any);

    // Assert
    expect(authService.register).not.toHaveBeenCalled();
  });

  it('should return true when passwords do not match', () => {
    // Arrange
    component.password = '123';
    component.confirmPassword = '456';

    // Act
    const result = component.passwordsDoNotMatch();

    // Assert
    expect(result).toBeTrue();
  });

  it('should return false when passwords match but are too short', () => {
    // Arrange
    component.password = '123';
    component.confirmPassword = '123';

    // Act
    const result = component.passwordsDoNotMatch();

    // Assert
    expect(result).toBeFalse();
  });


  // SUCCESS CASE
  it('should call authService.register with correct FormData when valid', () => {
    // Arrange
    authService.register.and.returnValue(of(undefined));

    component.firstName = 'John';
    component.lastName = 'Doe';
    component.email = 'john@example.com';
    component.phone = '+123456789';
    component.password = 'password123';
    component.confirmPassword = 'password123';
    component.address = '123 Main St';
    component.emailAvailable = true;

    // Act
    component.register({ invalid: false, controls: {} } as any);

    // Assert
    expect(authService.register).toHaveBeenCalled();

    const formDataArg = authService.register.calls.mostRecent().args[0] as FormData;

    expect(formDataArg.get('email')).toBe('john@example.com');
    expect(formDataArg.get('password')).toBe('password123');
    expect(formDataArg.get('firstName')).toBe('John');
    expect(formDataArg.get('lastName')).toBe('Doe');
    expect(formDataArg.get('address')).toBe('123 Main St');
    expect(formDataArg.get('phoneNumber')).toBe('+123456789');
  });

  it('should show success popup on successful registration', () => {
    // Arrange
    authService.register.and.returnValue(of(undefined));
    const cdrSpy = spyOn(component['cdr'], 'detectChanges');

    component.firstName = 'John';
    component.lastName = 'Doe';
    component.email = 'john@example.com';
    component.phone = '+123456789';
    component.password = 'password123';
    component.confirmPassword = 'password123';
    component.address = '123 Main St';
    component.emailAvailable = true;

    // Act
    component.register({ invalid: false, controls: {} } as any);

    // Assert
    expect(component.showPopup).toBeTrue();
    expect(component.popupSuccess).toBeTrue();
    expect(component.popupTitle).toBe('Success');
    expect(cdrSpy).toHaveBeenCalled();
  });

  // ERROR CASE
  it('should show error popup on registration failure', () => {
    // Arrange
    authService.register.and.returnValue(
      throwError(() => new Error('Registration failed'))
    );

    const cdrSpy = spyOn(component['cdr'], 'detectChanges');

    component.firstName = 'John';
    component.lastName = 'Doe';
    component.email = 'john@example.com';
    component.phone = '+123456789';
    component.password = 'password123';
    component.confirmPassword = 'password123';
    component.address = '123 Main St';
    component.emailAvailable = true;

    // Act
    component.register({ invalid: false, controls: {} } as any);

    // Assert
    expect(component.showPopup).toBeTrue();
    expect(component.popupSuccess).toBeFalse();
    expect(component.popupTitle).toBe('Error');
    expect(cdrSpy).toHaveBeenCalled();
  });

  // EMAIL CHECK
  it('should check email availability', () => {
    // Arrange
    authService.checkEmailAvailability.and.returnValue(
      of({ available: true })
    );
    const cdrSpy = spyOn(component['cdr'], 'detectChanges');
    component.email = 'test@example.com';

    // Act
    component.checkEmailAvailability();

    // Assert
    expect(authService.checkEmailAvailability)
      .toHaveBeenCalledWith('test@example.com');
    expect(component.emailAvailable).toBeTrue();
    expect(cdrSpy).toHaveBeenCalled();
  });

  // FILE SELECTION
  it('should set selectedProfilePicture when file selected', () => {
    // Arrange
    const mockFile = new File([''], 'profile.jpg', { type: 'image/jpeg' });

    // Act
    component.onProfilePictureSelected({
      target: { files: [mockFile] }
    } as any);

    // Assert
    expect(component.selectedProfilePicture).toBe(mockFile);
  });

  // POPUP
  it('should close popup and navigate on success', () => {
    // Arrange
    component.popupSuccess = true;

    // Act
    component.closePopup();

    // Assert
    expect(component.showPopup).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should call showSuccess helper', () => {
    // Arrange

    // Act
    component.showSuccess('Test success');

    // Assert
    expect(component.popupTitle).toBe('Success');
    expect(component.popupMessage).toBe('Test success');
    expect(component.popupSuccess).toBeTrue();
    expect(component.showPopup).toBeTrue();
  });

  it('should call showError helper', () => {
    // Arrange

    // Act
    component.showError('Test error');

    // Assert
    expect(component.popupTitle).toBe('Error');
    expect(component.popupMessage).toBe('Test error');
    expect(component.popupSuccess).toBeFalse();
    expect(component.showPopup).toBeTrue();
  });

});
