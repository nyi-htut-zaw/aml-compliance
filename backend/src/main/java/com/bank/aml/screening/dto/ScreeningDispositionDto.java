package com.bank.aml.screening.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ScreeningDispositionDto {

    @NotBlank(message = "Disposition is required")
    @Pattern(regexp = "FALSE_POSITIVE|TRUE_POSITIVE|ESCALATED", message = "Invalid disposition status")
    private String disposition;

    @NotBlank(message = "Disposition rational notes are required")
    private String notes;

    public ScreeningDispositionDto() {}

    public ScreeningDispositionDto(String disposition, String notes) {
        this.disposition = disposition;
        this.notes = notes;
    }

    public String getDisposition() { return disposition; }
    public void setDisposition(String disposition) { this.disposition = disposition; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
