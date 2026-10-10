package com.bank.aml.reporting.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ReportDto {
    private UUID id;
    private String reportNumber;
    private String reportType;
    private UUID caseId;
    private String caseNumber;
    private String status;
    private String jurisdictionCountry;
    private String reportingInstitution;
    private String subjectName;
    private String narrative;
    private BigDecimal totalAmountUsd;
    private String reportJson;
    private String preparedByUsername;
    private String approvedByUsername;
    private Instant submittedAt;
    private Instant createdAt;
    private Instant updatedAt;

    public ReportDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getReportNumber() { return reportNumber; }
    public void setReportNumber(String reportNumber) { this.reportNumber = reportNumber; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getJurisdictionCountry() { return jurisdictionCountry; }
    public void setJurisdictionCountry(String jurisdictionCountry) { this.jurisdictionCountry = jurisdictionCountry; }
    public String getReportingInstitution() { return reportingInstitution; }
    public void setReportingInstitution(String reportingInstitution) { this.reportingInstitution = reportingInstitution; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getNarrative() { return narrative; }
    public void setNarrative(String narrative) { this.narrative = narrative; }
    public BigDecimal getTotalAmountUsd() { return totalAmountUsd; }
    public void setTotalAmountUsd(BigDecimal totalAmountUsd) { this.totalAmountUsd = totalAmountUsd; }
    public String getReportJson() { return reportJson; }
    public void setReportJson(String reportJson) { this.reportJson = reportJson; }
    public String getPreparedByUsername() { return preparedByUsername; }
    public void setPreparedByUsername(String preparedByUsername) { this.preparedByUsername = preparedByUsername; }
    public String getApprovedByUsername() { return approvedByUsername; }
    public void setApprovedByUsername(String approvedByUsername) { this.approvedByUsername = approvedByUsername; }
    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
