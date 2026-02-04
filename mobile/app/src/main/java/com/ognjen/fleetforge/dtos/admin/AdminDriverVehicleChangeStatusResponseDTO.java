package com.ognjen.fleetforge.dtos.admin;

import com.ognjen.fleetforge.enums.InformationChangeRequestStatus;

public class AdminDriverVehicleChangeStatusResponseDTO {
    InformationChangeRequestStatus status;
    Long id;

    public InformationChangeRequestStatus getStatus() {
        return status;
    }

    public void setStatus(InformationChangeRequestStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
