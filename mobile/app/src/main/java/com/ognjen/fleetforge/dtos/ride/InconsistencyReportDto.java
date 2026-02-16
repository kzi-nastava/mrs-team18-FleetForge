package com.ognjen.fleetforge.dtos.ride;

public class InconsistencyReportDto {
    private Long reportId;
    private String message;
    private String reportedAt;

    public InconsistencyReportDto() {}

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(String reportedAt) {
        this.reportedAt = reportedAt;
    }
}