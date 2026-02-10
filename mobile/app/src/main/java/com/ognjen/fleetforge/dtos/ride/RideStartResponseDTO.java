package com.ognjen.fleetforge.dtos.ride;

import com.ognjen.fleetforge.enums.RideStatus;

public class RideStartResponseDTO {
    private Long id;
    private RideStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }
}
