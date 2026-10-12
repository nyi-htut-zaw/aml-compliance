package com.bank.aml.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class KycDecisionDto {

    @NotBlank(message = "Decision is required")
    @Pattern(regexp = "APPROVED|REJECTED|ESCALATED_EDD", message = "Invalid KYC decision")
    private String decision;

    private String decisionNotes;
    private String newRiskRating; // LOW, MEDIUM, HIGH, CRITICAL

    public KycDecisionDto() {}

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getDecisionNotes() { return decisionNotes; }
    public void setDecisionNotes(String decisionNotes) { this.decisionNotes = decisionNotes; }
    public String getNewRiskRating() { return newRiskRating; }
    public void setNewRiskRating(String newRiskRating) { this.newRiskRating = newRiskRating; }
}
