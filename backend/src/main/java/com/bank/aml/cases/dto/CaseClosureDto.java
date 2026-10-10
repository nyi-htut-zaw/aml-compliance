package com.bank.aml.cases.dto;

import jakarta.validation.constraints.NotBlank;

public class CaseClosureDto {
    @NotBlank
    private String decision; // CLEARED, SUSPICIOUS, FILED_STR

    @NotBlank
    private String rationale;

    private String investigationSummary;

    public CaseClosureDto() {}

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }
    public String getInvestigationSummary() { return investigationSummary; }
    public void setInvestigationSummary(String investigationSummary) { this.investigationSummary = investigationSummary; }
}
