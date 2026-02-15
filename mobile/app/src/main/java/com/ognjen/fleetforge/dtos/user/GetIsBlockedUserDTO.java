package com.ognjen.fleetforge.dtos.user;

public class GetIsBlockedUserDTO {
    private boolean blocked;
    private String reason;

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
