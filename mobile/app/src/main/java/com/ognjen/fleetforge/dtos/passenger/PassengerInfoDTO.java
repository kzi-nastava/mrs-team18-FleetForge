package com.ognjen.fleetforge.dtos.passenger;

public class PassengerInfoDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profileImage;

    public PassengerInfoDTO() {
    }

    public PassengerInfoDTO(Long id, String firstName, String lastName, String phoneNumber, String profileImage) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "PassengerInfoDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}