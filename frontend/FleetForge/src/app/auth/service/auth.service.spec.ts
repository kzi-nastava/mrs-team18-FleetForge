import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SidebarService } from '../../navigation/sidebar/sidebar.service';
import { Router } from '@angular/router';

describe('AuthService (Registration Related Methods)', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  let sidebarService: jasmine.SpyObj<SidebarService>;
  let router: jasmine.SpyObj<Router>;

  const API_URL = 'http://localhost:8080/api/auth';

  beforeEach(() => {
    sidebarService = jasmine.createSpyObj('SidebarService', [
      'setAuthenticated',
      'setUserRole'
    ]);

    router = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        { provide: SidebarService, useValue: sidebarService },
        { provide: Router, useValue: router }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // REGISTER
  it('should send POST request to /register with FormData', () => {
    // Arrange
    const formData = new FormData();
    formData.append('email', 'john@example.com');
    formData.append('password', 'password123');

    // Act
    service.register(formData).subscribe();

    // Assert
    const req = httpMock.expectOne(`${API_URL}/register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBe(formData);

    req.flush(null);
  });

  it('should handle successful registration response', () => {
    // Arrange
    const formData = new FormData();
    formData.append('email', 'john@example.com');

    let responseReceived = false;

    // Act
    service.register(formData).subscribe(() => {
      responseReceived = true;
    });

    const req = httpMock.expectOne(`${API_URL}/register`);
    req.flush(null);

    // Assert
    expect(responseReceived).toBeTrue();
  });

  it('should propagate error if registration fails', () => {
    // Arrange
    const formData = new FormData();
    formData.append('email', 'john@example.com');

    let errorResponse: any;

    // Act
    service.register(formData).subscribe({
      error: (err) => (errorResponse = err)
    });

    const req = httpMock.expectOne(`${API_URL}/register`);
    req.flush('Registration failed', {
      status: 400,
      statusText: 'Bad Request'
    });

    // Assert
    expect(errorResponse.status).toBe(400);
  });

  // CHECK EMAIL AVAILABILITY
  it('should send GET request to /email-availability with email param', () => {
    // Arrange
    const email = 'test@example.com';

    // Act
    service.checkEmailAvailability(email).subscribe();

    // Assert
    const req = httpMock.expectOne(
      `${API_URL}/email-availability?email=${email}`
    );

    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('email')).toBe(email);

    req.flush({ available: true });
  });

  it('should return available: true when email is available', () => {
    // Arrange
    const email = 'test@example.com';
    let result: any;

    // Act
    service.checkEmailAvailability(email).subscribe(res => {
      result = res;
    });

    const req = httpMock.expectOne(
      `${API_URL}/email-availability?email=${email}`
    );

    req.flush({ available: true });

    // Assert
    expect(result.available).toBeTrue();
  });

  it('should return available: false when email is taken', () => {
    // Arrange
    const email = 'taken@example.com';
    let result: any;

    // Act
    service.checkEmailAvailability(email).subscribe(res => {
      result = res;
    });

    const req = httpMock.expectOne(
      `${API_URL}/email-availability?email=${email}`
    );

    req.flush({ available: false });

    // Assert
    expect(result.available).toBeFalse();
  });

  it('should propagate error if email availability request fails', () => {
    // Arrange
    const email = 'error@example.com';
    let errorResponse: any;

    // Act
    service.checkEmailAvailability(email).subscribe({
      error: (err) => (errorResponse = err)
    });

    const req = httpMock.expectOne(
      `${API_URL}/email-availability?email=${email}`
    );

    req.flush('Server error', {
      status: 500,
      statusText: 'Internal Server Error'
    });

    // Assert
    expect(errorResponse.status).toBe(500);
  });
});
