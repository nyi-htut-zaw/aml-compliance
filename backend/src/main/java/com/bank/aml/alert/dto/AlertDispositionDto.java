package com.bank.aml.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AlertDispositionDto {

    @NotBlank(message = "Disposition is required")
    @Pattern(regexp = "CLOSED_FALSE_POSITIVE|CLOSED_TRUE_POSITIVE|ESCALATED|CONVERT_TO_CASE", message = "Invalid alert disposition")
    private String disposition;

    @NotBlank(message = "Disposition notes are required")
    private String notes;

    public AlertDispositionDto() {}

    public AlertDispositionDto(String disposition, String notes) {
        this.disposition = disposition;
        this.notes = notes;
    }

    public String getDisposition() { return disposition; }
    public void setDisposition(String disposition) { this.disposition = disposition; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
