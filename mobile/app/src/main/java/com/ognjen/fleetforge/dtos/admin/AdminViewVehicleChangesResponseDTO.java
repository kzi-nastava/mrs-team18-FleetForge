package com.ognjen.fleetforge.dtos.admin;

import com.ognjen.fleetforge.enums.VehicleType;

public class AdminViewVehicleChangesResponseDTO {
    private Long vehicleId;
    private Long requestId;

    private String firstName;
    private String lastName;
    private String model;
    private VehicleType type;
    private String registrationNumber;
    private int space;
    private boolean babySeat;
    private boolean petFriendly;

    private String newModel;
    private VehicleType newType;
    private String newRegistrationNumber;
    private int newSpace;
    private boolean newBabySeat;
    private boolean newPetFriendly;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public boolean isBabySeat() {
        return babySeat;
    }

    public void setBabySeat(boolean babySeat) {
        this.babySeat = babySeat;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public String getNewModel() {
        return newModel;
    }

    public void setNewModel(String newModel) {
        this.newModel = newModel;
    }

    public VehicleType getNewType() {
        return newType;
    }

    public void setNewType(VehicleType newType) {
        this.newType = newType;
    }

    public String getNewRegistrationNumber() {
        return newRegistrationNumber;
    }

    public void setNewRegistrationNumber(String newRegistrationNumber) {
        this.newRegistrationNumber = newRegistrationNumber;
    }

    public int getNewSpace() {
        return newSpace;
    }

    public void setNewSpace(int newSpace) {
        this.newSpace = newSpace;
    }

    public boolean isNewBabySeat() {
        return newBabySeat;
    }

    public void setNewBabySeat(boolean newBabySeat) {
        this.newBabySeat = newBabySeat;
    }

    public boolean isNewPetFriendly() {
        return newPetFriendly;
    }

    public void setNewPetFriendly(boolean newPetFriendly) {
        this.newPetFriendly = newPetFriendly;
    }
}
