export interface DriverProfileChangeRequest {
    id: number;
  oldFristName: string;
  newFirstName: string;
  oldLastName: string;
  newLastName: string;
  oldEmail: string;
  newEmail: string;
  oldPhoneNumber: string;
  newPhoneNumber: string;
  oldAddress: string;
  newAddress: string;
  oldProfilePictureUrl: string;
  newProfilePictureUrl: string;
}