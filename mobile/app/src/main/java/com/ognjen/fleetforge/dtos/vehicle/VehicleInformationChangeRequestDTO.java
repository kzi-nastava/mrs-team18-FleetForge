package com.ognjen.fleetforge.dtos.vehicle;

import com.ognjen.fleetforge.enums.VehicleType;

public class VehicleInformationChangeRequestDTO {
    private String newModel;
    private VehicleType newType;
    private String newRegistrationNumber;
    private int newSpace;
    private boolean newBabySeat;
    private boolean newPetFriendly;

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
