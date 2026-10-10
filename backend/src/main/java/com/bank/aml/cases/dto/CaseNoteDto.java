package com.bank.aml.cases.dto;

import jakarta.validation.constraints.NotBlank;

public class CaseNoteDto {
    @NotBlank
    private String content;

    private boolean confidential;

    public CaseNoteDto() {}

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isConfidential() { return confidential; }
    public void setConfidential(boolean confidential) { this.confidential = confidential; }
}
