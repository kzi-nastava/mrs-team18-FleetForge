package com.ognjen.fleetforge.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DriverProfileChangeRequest implements Parcelable{

    private boolean isExtended;

    public boolean isExpanded() {
        return isExtended;
    }

    public void setExpanded(boolean extended) {
        isExtended = extended;
    }
    private Long requestId;
    private Long driverId;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private String newPhoneNumber;
    private String newAddress;
    private String oldFirstName;
    private String oldLastName;
    private String oldEmail;
    private String oldPhoneNumber;
    private String oldAddress;

    public String getOldFirstName() {
        return oldFirstName;
    }

    public void setOldFirstName(String oldFirstName) {
        this.oldFirstName = oldFirstName;
    }

    public String getOldLastName() {
        return oldLastName;
    }

    public void setOldLastName(String oldLastName) {
        this.oldLastName = oldLastName;
    }

    public String getOldEmail() {
        return oldEmail;
    }

    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }

    public String getOldPhoneNumber() {
        return oldPhoneNumber;
    }

    public void setOldPhoneNumber(String oldPhoneNumber) {
        this.oldPhoneNumber = oldPhoneNumber;
    }

    public String getOldAddress() {
        return oldAddress;
    }

    public void setOldAddress(String oldAddress) {
        this.oldAddress = oldAddress;
    }




    public String getNewFirstName(){
        return newFirstName;
    }
    public String getNewLastName() {
        return newLastName;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public String getNewPhoneNumber() {
        return newPhoneNumber;
    }

    public String getNewAddress() {
        return newAddress;
    }





    public void setNewFirstName(String newFirstName) {
        this.newFirstName = newFirstName;
    }

    public void setNewLastName(String newLastName) {
        this.newLastName = newLastName;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public void setNewPhoneNumber(String newPhoneNumber) {
        this.newPhoneNumber = newPhoneNumber;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeBoolean(isExtended);
        dest.writeLong(requestId);
        dest.writeLong(driverId);
        dest.writeString(newFirstName);
        dest.writeString(oldFirstName);
        dest.writeString(newLastName);
        dest.writeString(oldLastName);
        dest.writeString(oldEmail);
        dest.writeString(newEmail);
        dest.writeString(newPhoneNumber);
        dest.writeString(oldPhoneNumber);
        dest.writeString(oldAddress);
        dest.writeString(newAddress);
    }
}
