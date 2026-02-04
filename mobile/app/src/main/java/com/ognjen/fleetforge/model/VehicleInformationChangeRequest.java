package com.ognjen.fleetforge.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.ognjen.fleetforge.enums.VehicleType;

public class VehicleInformationChangeRequest implements Parcelable {

    private Long vehicleId;
    private Long requestId;

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

    private String firstName;
    private boolean isExpanded;
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean extended) {
        isExpanded = extended;
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

    public String getOldModel() {
        return oldModel;
    }

    public void setOldModel(String oldModel) {
        this.oldModel = oldModel;
    }

    public VehicleType getOldType() {
        return oldType;
    }

    public void setOldType(VehicleType oldType) {
        this.oldType = oldType;
    }

    public String getOldRegistrationNumber() {
        return oldRegistrationNumber;
    }

    public void setOldRegistrationNumber(String oldRegistrationNumber) {
        this.oldRegistrationNumber = oldRegistrationNumber;
    }

    public int getOldSpace() {
        return oldSpace;
    }

    public void setOldSpace(int oldSpace) {
        this.oldSpace = oldSpace;
    }

    public boolean isOldBabySeat() {
        return oldBabySeat;
    }

    public void setOldBabySeat(boolean oldBabySeat) {
        this.oldBabySeat = oldBabySeat;
    }

    public boolean isOldPetFriendly() {
        return oldPetFriendly;
    }

    public void setOldPetFriendly(boolean oldPetFriendly) {
        this.oldPetFriendly = oldPetFriendly;
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

    private String lastName;


    private String oldModel;
    private VehicleType oldType;
    private String oldRegistrationNumber;
    private int oldSpace;
    private boolean oldBabySeat;
    private boolean oldPetFriendly;

    private String newModel;
    private VehicleType newType;
    private String newRegistrationNumber;
    private int newSpace;
    private boolean newBabySeat;
    private boolean newPetFriendly;
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeBoolean(isExpanded);
        dest.writeLong(requestId);
        dest.writeLong(vehicleId);
        dest.writeString(oldModel);
        dest.writeString(oldType.toString());
        dest.writeString(oldRegistrationNumber);
        dest.writeInt(oldSpace);
        dest.writeBoolean(oldBabySeat);
        dest.writeBoolean(oldPetFriendly);

        dest.writeString(newModel);
        dest.writeString(newType.toString());
        dest.writeString(newRegistrationNumber);
        dest.writeInt(newSpace);
        dest.writeBoolean(newBabySeat);
        dest.writeBoolean(newPetFriendly);

    }
}
