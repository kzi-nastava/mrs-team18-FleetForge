package com.ognjen.fleetforge.dtos.driver;

import com.ognjen.fleetforge.dtos.vehicle.VehicleCreateRequestDTO;

public class DriverCreateRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    VehicleCreateRequestDTO vehicle;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public VehicleCreateRequestDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleCreateRequestDTO vehicle) {
        this.vehicle = vehicle;
    }
}
