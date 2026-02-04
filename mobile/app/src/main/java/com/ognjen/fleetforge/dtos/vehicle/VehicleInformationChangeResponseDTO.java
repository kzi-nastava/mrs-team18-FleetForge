package com.ognjen.fleetforge.dtos.vehicle;

import com.ognjen.fleetforge.enums.InformationChangeRequestStatus;

import java.time.LocalDateTime;

public class VehicleInformationChangeResponseDTO {
    InformationChangeRequestStatus status;
    LocalDateTime createdAt;
    Long requestId;

    public InformationChangeRequestStatus getStatus() {
        return status;
    }

    public void setStatus(InformationChangeRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
