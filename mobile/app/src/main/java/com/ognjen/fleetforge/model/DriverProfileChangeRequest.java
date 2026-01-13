package com.ognjen.fleetforge.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DriverProfileChangeRequest implements Parcelable{
    protected DriverProfileChangeRequest(Parcel in){
        isExtended=in.readBoolean();
        newFirstName=in.readString();
        oldFirstName=in.readString();
        newLastName=in.readString();
        oldLastName=in.readString();
        oldEmail=in.readString();
        newEmail=in.readString();
        newPhoneNumber=in.readString();
        oldPhoneNumber=in.readString();
        oldAddress=in.readString();
        newAddress=in.readString();
        oldProfilePicture=in.readString();
        newProfilePicture=in.readString();
    }

    private boolean isExtended;

    public boolean isExpanded() {
        return isExtended;
    }

    public void setExpanded(boolean extended) {
        isExtended = extended;
    }

    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private String newPhoneNumber;
    private String newAddress;
    private String newProfilePicture;
    private String oldFirstName;
    private String oldLastName;
    private String oldEmail;
    private String oldPhoneNumber;
    private String oldAddress;
    private String oldProfilePicture;

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

    public String getOldProfilePicture() {
        return oldProfilePicture;
    }

    public void setOldProfilePicture(String oldProfilePicture) {
        this.oldProfilePicture = oldProfilePicture;
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

    public String getNewProfilePicture() {
        return newProfilePicture;
    }

    public DriverProfileChangeRequest(boolean isExtended,String newFirstName, String newLastName, String newEmail
    , String newPhoneNumber, String newAddress, String newProfilePicture, String oldFirstName, String oldLastName, String oldEmail,
                                      String oldPhoneNumber, String oldAddress, String oldProfilePicture){
        this.isExtended=isExtended;
        this.newFirstName=newFirstName;
        this.newLastName=newLastName;
        this.newEmail=newEmail;
        this.newPhoneNumber=newPhoneNumber;
        this.newAddress=newAddress;
        this.newProfilePicture=newProfilePicture;
        this.oldFirstName=oldFirstName;
        this.oldLastName=oldLastName;
        this.oldEmail=oldEmail;
        this.oldPhoneNumber=oldPhoneNumber;
        this.oldAddress=oldAddress;
        this.oldProfilePicture=oldProfilePicture;
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

    public void setNewProfilePicture(String newProfilePicture) {
        this.newProfilePicture = newProfilePicture;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeBoolean(isExtended);
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
        dest.writeString(oldProfilePicture);
        dest.writeString(newProfilePicture);
    }
}
