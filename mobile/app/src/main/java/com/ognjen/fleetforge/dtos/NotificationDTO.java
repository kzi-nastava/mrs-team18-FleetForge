package com.ognjen.fleetforge.dtos;

import com.google.gson.annotations.SerializedName;
import com.ognjen.fleetforge.enums.NotificationType;

import java.time.LocalDateTime;

public class NotificationDTO {
    @SerializedName("id")
    private Long id;
    @SerializedName("type")
    private NotificationType type;
    @SerializedName("message")
    private String message;
    @SerializedName("isRead")
    private Boolean isRead;
    @SerializedName("createdAt")
    private LocalDateTime createdAt;
    @SerializedName("rideId")
    private Long rideId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }
}
