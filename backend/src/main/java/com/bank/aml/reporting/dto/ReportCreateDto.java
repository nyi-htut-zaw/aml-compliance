package com.bank.aml.reporting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public class ReportCreateDto {
    @NotBlank
    private String reportType; // SAR, STR, CTR

    @NotNull
    private UUID caseId;

    @NotBlank
    private String jurisdictionCountry;

    @NotBlank
    private String narrative;

    @NotNull
    private BigDecimal totalAmountUsd;

    public ReportCreateDto() {}

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public String getJurisdictionCountry() { return jurisdictionCountry; }
    public void setJurisdictionCountry(String jurisdictionCountry) { this.jurisdictionCountry = jurisdictionCountry; }
    public String getNarrative() { return narrative; }
    public void setNarrative(String narrative) { this.narrative = narrative; }
    public BigDecimal getTotalAmountUsd() { return totalAmountUsd; }
    public void setTotalAmountUsd(BigDecimal totalAmountUsd) { this.totalAmountUsd = totalAmountUsd; }
}
