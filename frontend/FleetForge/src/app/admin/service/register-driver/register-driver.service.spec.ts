import { TestBed } from '@angular/core/testing';

import { RegisterDriverService } from './register-driver.service';
import { provideHttpClientTesting,HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { DriverCreateRequestDTO, DriverCreateResponseDTO } from '../../../shared/dtos/driver.dtos';
import { VehicleType } from '../../../shared/dtos/vehicle.dtos';

describe('RegisterDriverService', () => {
  let service: RegisterDriverService;
  let httpMock: HttpTestingController;
  const mockRequest: DriverCreateRequestDTO = {
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
  };
  const mockResponse: DriverCreateResponseDTO = { driverId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        RegisterDriverService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(RegisterDriverService);
    httpMock = TestBed.inject(HttpTestingController);
  });
  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
    expect(httpMock).toBeTruthy();
  });

  describe('registerDriver', () => {
    it('should send a POST request to the correct URL with the correct request body', () => {
      service.registerDriver(mockRequest).subscribe(response => {
        expect(response.driverId).toBe(1);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/drivers');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockRequest);
      expect(req.request.url).toBe('http://localhost:8080/api/drivers');
      req.flush(mockResponse);
    });
  });
describe('uploadProfilePicture', () => {
    it('should send a POST request to the correct URL with driver id and file in body', () => {
      const formData = new FormData();
      formData.append('file', new File([''], 'test.jpg'));

      service.uploadProfilePicture(formData, 1).subscribe(response => {
        expect(response).toEqual({});
      });

      const req = httpMock.expectOne('http://localhost:8080/api/users/upload-profile-picture/1');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toBe(formData);
      expect(req.request.url).toContain('/1');
      expect(req.request.url).toBe('http://localhost:8080/api/users/upload-profile-picture/1');
      req.flush({});
    });
  });
});
