package com.bank.aml.iam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MakerCheckerDecisionDto {

    @NotBlank(message = "Decision is required")
    @Pattern(regexp = "APPROVED|REJECTED", message = "Decision must be APPROVED or REJECTED")
    private String decision;

    private String comments;

    public MakerCheckerDecisionDto() {}

    public MakerCheckerDecisionDto(String decision, String comments) {
        this.decision = decision;
        this.comments = comments;
    }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
