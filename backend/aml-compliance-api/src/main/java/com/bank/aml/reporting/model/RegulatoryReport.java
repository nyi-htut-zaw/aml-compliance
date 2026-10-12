package com.bank.aml.reporting.model;

import com.bank.aml.cases.model.Case;
import com.bank.aml.iam.model.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "regulatory_reports")
public class RegulatoryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "report_number", nullable = false, unique = true, length = 50)
    private String reportNumber;

    @Column(name = "report_type", nullable = false, length = 20)
    private String reportType; // SAR, STR, CTR

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case sourceCase;

    @Column(nullable = false, length = 30)
    private String status = "DRAFT"; // DRAFT, UNDER_REVIEW, APPROVED, EXPORTED, WITHDRAWN

    @Column(name = "jurisdiction_country", nullable = false, length = 3)
    private String jurisdictionCountry;

    @Column(name = "reporting_institution", nullable = false)
    private String reportingInstitution;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "narrative", columnDefinition = "TEXT", nullable = false)
    private String narrative;

    @Column(name = "total_amount_usd", nullable = false)
    private java.math.BigDecimal totalAmountUsd;

    @Column(name = "report_json", columnDefinition = "TEXT")
    private String reportJson; // Exported JSON payload (goAML / FinCEN format)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prepared_by_id")
    private User preparedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public RegulatoryReport() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getReportNumber() { return reportNumber; }
    public void setReportNumber(String reportNumber) { this.reportNumber = reportNumber; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public Case getSourceCase() { return sourceCase; }
    public void setSourceCase(Case sourceCase) { this.sourceCase = sourceCase; }
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
    public java.math.BigDecimal getTotalAmountUsd() { return totalAmountUsd; }
    public void setTotalAmountUsd(java.math.BigDecimal totalAmountUsd) { this.totalAmountUsd = totalAmountUsd; }
    public String getReportJson() { return reportJson; }
    public void setReportJson(String reportJson) { this.reportJson = reportJson; }
    public User getPreparedBy() { return preparedBy; }
    public void setPreparedBy(User preparedBy) { this.preparedBy = preparedBy; }
    public User getApprovedBy() { return approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }
    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
