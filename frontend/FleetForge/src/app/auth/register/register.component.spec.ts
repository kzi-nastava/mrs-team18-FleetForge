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
    expect(component.firstName).toBe('');
    expect(component.lastName).toBe('');
    expect(component.email).toBe('');
    expect(component.phone).toBe('');
    expect(component.password).toBe('');
    expect(component.confirmPassword).toBe('');
    expect(component.address).toBe('');
    expect(component.selectedProfilePicture).toBeNull();
  });

  it('should initialize with submitted as false', () => {
    expect(component.submitted).toBeFalse();
  });

  it('should clear localStorage on init', () => {
    const spy = spyOn(Storage.prototype, 'removeItem');

    const newFixture = TestBed.createComponent(RegisterComponent);
    newFixture.detectChanges();

    expect(spy).toHaveBeenCalledWith('token');
    expect(spy).toHaveBeenCalledWith('role');
  });

  // FORM VALIDATION
  it('should mark form as invalid when required fields are empty', () => {
    const form: any = {
      invalid: true,
      controls: {
        firstName: { markAsTouched: jasmine.createSpy() },
        lastName: { markAsTouched: jasmine.createSpy() },
        email: { markAsTouched: jasmine.createSpy() }
      }
    };

    component.register(form);

    expect(component.submitted).toBeTrue();
    expect(form.controls.firstName.markAsTouched).toHaveBeenCalled();
    expect(form.controls.lastName.markAsTouched).toHaveBeenCalled();
    expect(form.controls.email.markAsTouched).toHaveBeenCalled();
  });

  it('should not submit when passwords do not match', () => {
    component.firstName = 'John';
    component.lastName = 'Doe';
    component.email = 'john@example.com';
    component.phone = '+123456789';
    component.password = 'password123';
    component.confirmPassword = 'password456';
    component.address = '123 Main St';

    component.register({ invalid: false, controls: {} } as any);

    expect(authService.register).not.toHaveBeenCalled();
  });

  it('should return true when passwords do not match', () => {
    component.password = '123';
    component.confirmPassword = '456';
    expect(component.passwordsDoNotMatch()).toBeTrue();
  });

  it('should return false when passwords match', () => {
    component.password = '123';
    component.confirmPassword = '123';
    expect(component.passwordsDoNotMatch()).toBeFalse();
  });

  // SUCCESS CASE
  it('should call authService.register with correct FormData when valid', () => {
    authService.register.and.returnValue(of(undefined));

    component.firstName = 'John';
    component.lastName = 'Doe';
    component.email = 'john@example.com';
    component.phone = '+123456789';
    component.password = 'password123';
    component.confirmPassword = 'password123';
    component.address = '123 Main St';
    component.emailAvailable = true;

    component.register({ invalid: false, controls: {} } as any);

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

    component.register({ invalid: false, controls: {} } as any);

    expect(component.showPopup).toBeTrue();
    expect(component.popupSuccess).toBeTrue();
    expect(component.popupTitle).toBe('Success');
    expect(cdrSpy).toHaveBeenCalled();
  });
});
