package com.ognjen.fleetforge.dtos.ride;

public class InconsistencyReportResponseDTO {
    private Long reportId;
    private String message;
    private String reportedAt;

    public InconsistencyReportResponseDTO() {
    }

    public InconsistencyReportResponseDTO(Long reportId, String message, String reportedAt) {
        this.reportId = reportId;
        this.message = message;
        this.reportedAt = reportedAt;
    }

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

